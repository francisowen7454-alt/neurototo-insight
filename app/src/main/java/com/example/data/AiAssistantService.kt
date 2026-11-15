package com.example.data

import com.example.model.ChatMessage
import com.example.model.ChildProfile
import com.example.model.MilestoneCategory
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

data class PediatricAnalysisResult(
    val answer: String,
    val isWarning: Boolean,
    val recommendedDoctor: String?,
    val relatedMilestoneCategory: MilestoneCategory? = null,
    val relatedMilestoneId: String? = null,
    val relatedMilestoneTitle: String? = null,
    val developmentalTipSwahili: String? = null,
    val developmentalTipEnglish: String? = null,
    val suggestedObservationNote: String? = null
)

object AiAssistantService {

    private val geminiModel by lazy {
        try {
            Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash"
            )
        } catch (_: Exception) {
            null
        }
    }

    suspend fun generateResponse(
        prompt: String,
        child: ChildProfile,
        language: String
    ): ChatMessage = withContext(Dispatchers.IO) {
        val trimmedPrompt = prompt.trim()
        var aiAnswer: String? = null

        // 1. Attempt generation via Firebase AI SDK
        try {
            val systemContext = if (language == "sw") {
                """
                Wewe ni NeuroToto Insight AI, msaidizi maalum wa afya ya mfumo wa fahamu (pediatric neurology) na malezi ya kidijitali kwa wazazi wa Afrika Mashariki.
                Taarifa za mtoto: Jina: ${child.name}, Umri: miezi ${child.ageMonths}, Jinsia: ${child.gender}.
                Toa mwongozo wa kitaalamu, lugha nyepesi ya Kiswahili yenye upendo na matumaini, ukielezea sayansi ya ubongo, lishe ya asili (kama samaki, viazi lishe, mayai), na mbinu za kupunguza simu/TV.
                Mwishoni weka tahadhari kwamba ushauri huu haulengi kuchukua nafasi ya uchunguzi wa daktari.
                """.trimIndent()
            } else {
                """
                You are NeuroToto Insight AI, a specialized pediatric neurological health and digital parenting assistant for East African families.
                Child Profile: Name: ${child.name}, Age: ${child.ageMonths} months, Gender: ${child.gender}.
                Provide compassionate, evidence-based pediatric neurology advice, clear explanations of brain development, indigenous brain nutrition, and healthy screen-time limits.
                Always remind the parent that this does not replace a clinical examination.
                """.trimIndent()
            }

            val fullPrompt = "$systemContext\n\nSwali la Mzazi: $trimmedPrompt"

            geminiModel?.let { model ->
                withTimeout(15_000L) {
                    val response = model.generateContent(fullPrompt)
                    val text = response.text
                    if (!text.isNullOrBlank()) {
                        aiAnswer = text
                    }
                }
            }
        } catch (_: Exception) {
            // Handled securely; fallback to pediatric clinical engine
        }

        // 2. Clinical Pediatric Developmental Engine (with milestone mapping)
        val analysis = analyzePediatricQuery(trimmedPrompt, child, language)
        val finalAnswerText = aiAnswer ?: analysis.answer

        ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            sender = "toto_ai",
            text = finalAnswerText,
            timestamp = "Sasa hivi",
            timestampMillis = System.currentTimeMillis(),
            isWarning = analysis.isWarning || isQueryWarning(trimmedPrompt),
            recommendedDoctorCategory = analysis.recommendedDoctor,
            isAiGenerated = true,
            relatedMilestoneCategory = analysis.relatedMilestoneCategory,
            relatedMilestoneId = analysis.relatedMilestoneId,
            relatedMilestoneTitle = analysis.relatedMilestoneTitle,
            developmentalTipSwahili = analysis.developmentalTipSwahili,
            developmentalTipEnglish = analysis.developmentalTipEnglish,
            suggestedObservationNote = analysis.suggestedObservationNote,
            isMilestoneLogged = false
        )
    }

    private fun isQueryWarning(query: String): Boolean {
        val q = query.lowercase()
        return q.contains("mshtuko") || q.contains("seizure") || q.contains("kuzimia") ||
                q.contains("haitikii") || q.contains("kutembea") || q.contains("regression") ||
                q.contains("haongei") || q.contains("autism") || q.contains("usonji")
    }

    private fun analyzePediatricQuery(
        query: String,
        child: ChildProfile,
        lang: String
    ): PediatricAnalysisResult {
        val q = query.lowercase()

        // 1. Language and speech delay
        if (q.contains("ongea") || q.contains("tamka") || q.contains("lugha") || q.contains("speech") || q.contains("talk") || q.contains("maneno")) {
            val swahili = """
                Habari mzazi wa ${child.name}! Kuhusu ukuaji wa lugha na usemi kwa mtoto mwenye umri wa miezi ${child.ageMonths}:
                
                🔹 **Hatua za Kawaida za Neva:** Katika umri huu wa miezi ${child.ageMonths}, mtoto anatarajiwa kutumia maneno 5 hadi 10 yenye maana (kama 'Mama', 'Baba', 'Maji', 'Nipe') na kuelewa maagizo mepesi.
                
                🔹 **Mbinu 3 za Kuchochea Ukuaji wa Lugha:**
                1. **Punguza Skrini (Zero Passive Screen Time):** Skrini za simu na TV hutoa mwanga na sauti ya upande mmoja, ikizuia uwezo wa ubongo kujiunganisha kwa mazungumzo (Serve and Return).
                2. **Mzungumzishe kwa Vitendo:** Kila unachofanya, taja jina lake: "Hiki ni kijiko", "Hili ni parachichi tamu".
                3. **Msubiri Ajibu (Pause Technique):** Subiri sekunde 5-10 baada ya kumuuliza swali kabla ya kumpa kitu, ili kumpa fursa ya kutamka.
                
                ⚠️ **Tahadhari ya Daktari:** Iwapo mtoto hatazami machoni au haitikii anapoitwa, fanya uchunguzi wa mapema na Daktari Bingwa wa Neva (Pediatric Neurologist).
            """.trimIndent()

            val english = """
                Hello parent of ${child.name}! Regarding speech and language milestones for age ${child.ageMonths} months:
                
                🔹 **Neurological Milestone:** At ${child.ageMonths} months, children typically combine 5 to 10 intentional words with gestures and comprehend simple instructions.
                
                🔹 **Evidence-Based Language Guidance:**
                1. **Eliminate Passive Screens:** Television and video feeds disrupt conversational turn-taking pathways.
                2. **Narrate Daily Routines:** Continuously name objects, colors, and actions.
                3. **Expectant Pausing:** Allow 5-10 seconds for auditory processing before repeating prompts.
                
                ⚠️ **Clinical Warning:** If the child lacks reciprocal eye contact or does not respond to their name, consult a pediatric neurologist.
            """.trimIndent()

            return PediatricAnalysisResult(
                answer = if (lang == "sw") swahili else english,
                isWarning = true,
                recommendedDoctor = "Pediatric Neurologist",
                relatedMilestoneCategory = MilestoneCategory.SPEECH,
                relatedMilestoneId = "m_1_2_speech_words",
                relatedMilestoneTitle = if (lang == "sw") "Kutamka Maneno 5 Hadi 10 Yenye Maana" else "Saying 5 to 10 Meaningful Words",
                developmentalTipSwahili = "Zoezi: Zungumza naye ana kwa ana kwa dakika 15 bila simu, ukimtazama machoni.",
                developmentalTipEnglish = "Daily Exercise: Practice 15 minutes of device-free serve-and-return communication.",
                suggestedObservationNote = "Anafanya mazoezi ya kutamka maneno na kuiga sauti bila usumbufu wa simu au TV."
            )
        }

        // 2. Autism / Usonji / Joint Attention / Neurodiversity
        if (q.contains("usonji") || q.contains("autism") || q.contains("adhd") || q.contains("fahamu") || q.contains("neuro") || q.contains("akili") || q.contains("kidole") || q.contains("point") || q.contains("jina")) {
            val swahili = """
                Kuhusu utambuzi, afya ya mfumo wa fahamu, na ushirikishwaji wa hisia kwa ${child.name}:
                
                🧠 **Kuelekeza Kidole (Joint Attention):**
                Kitendo cha mtoto kuelekeza kidole kwenye kitu kinachomvutia (kama ndege, gari, au kinyago) kisha kukuangalia usoni kuona kama umeona, ni moja ya alama kubwa ya uimara wa mfumo wa fahamu na uwezo wa kijamii.
                
                🔍 **Dalili za Awali za Kuzingatia:**
                - Kutogeuka anapoitwa jina lake mara kwa mara licha ya kusikia milio mingine.
                - Kutotazama machoni (poor eye contact) wakati wa kuongea au kunyonyeshwa.
                - Kurudia vitendo vilevile (stimming) kama kuzungusha mikono au kupanga vitu kwa mstari mrefu.
                
                🌟 **Ushauri:** Ugunduzi wa mapema (Early Intervention) unampa mtoto fursa bora zaidi ya kuimarika. Unaweza kuwasiliana na Dkt. Amani Mwenda moja kwa moja kwenye orodha ya wataalamu wetu.
            """.trimIndent()

            val english = """
                Regarding neurodevelopmental health and joint attention for ${child.name}:
                
                🧠 **Joint Attention & Social Engagement:**
                Pointing to an interesting object and immediately looking back at a caregiver's eyes represents a major neurodevelopmental cornerstone of social cognition.
                
                🔍 **Key Early Developmental Markers:**
                - Inconsistent turning when their name is called.
                - Reduced or fleeting eye contact during interactive play.
                - Repetitive sensory soothing (hand flapping, spinning objects).
                
                🌟 **Clinical Guidance:** Early therapeutic support before age 3 radically enhances communicative pathways.
            """.trimIndent()

            return PediatricAnalysisResult(
                answer = if (lang == "sw") swahili else english,
                isWarning = true,
                recommendedDoctor = "Pediatric Neurologist",
                relatedMilestoneCategory = MilestoneCategory.COGNITIVE,
                relatedMilestoneId = "m_1_2_cog_point",
                relatedMilestoneTitle = if (lang == "sw") "Kuelekeza Kidole Kushirikisha Hisia (Joint Attention)" else "Pointing to Share Interest & Eye Contact",
                developmentalTipSwahili = "Cheza mchezo wa kuangalia picha na kumwambia: 'Tazama ndege yule!' ukielekeza kidole.",
                developmentalTipEnglish = "Encourage pointing during picture book reading by modeling shared attention.",
                suggestedObservationNote = "Anajaribu kuelekeza vitu anavyovipenda kwa kidole na kuangalia usoni."
            )
        }

        // 3. Motor skills: Walking, running, climbing, coordination
        if (q.contains("tembea") || q.contains("walk") || q.contains("simama") || q.contains("stand") || q.contains("kimbia") || q.contains("run") || q.contains("misuli") || q.contains("motor")) {
            val swahili = """
                Mwongozo wa Hatua za Misuli na Miondoko (Motor Milestones) kwa ${child.name} (${child.ageMonths} Miezi):
                
                🏃 **Hatua za Umri Huu:**
                Katika umri wa miezi ${child.ageMonths}, mtoto anapaswa kusimama na kutembea mwenyewe bila kushikiliwa, na kuanza kujaribu kukimbia au kuvuta vinyago anapotembea.
                
                💡 **Mazoezi ya Kuimarisha Miguu na Mgongo:**
                1. **Kutembea Bila Viatu (Barefoot Play):** Kumwacha mtoto atembee bila viatu ndani ya nyumba au kwenye nyasi safi kunajenga misuli ya nyayo na hisia za uwiano (proprioception).
                2. **Kupanda Ngazi Chini ya Ulinzi:** Mruhusu kupanda ngazi 2 au 3 huku ukimshika mkono mmoja ili kujenga nguvu ya mapaja.
                3. **Kusukuma/Kuvuta Vinyago:** Mpe boksi dogo lenye vitu vya wastani asukume akitembea.
            """.trimIndent()

            val english = """
                Gross Motor Developmental Guide for ${child.name} (${child.ageMonths} Months):
                
                🏃 **Physical Milestones:**
                At ${child.ageMonths} months, toddlers achieve independent walking, begin transitional running, and push/pull toys while cruising.
                
                💡 **Strengthening Activities:**
                1. **Barefoot Sensory Walking:** Walking barefoot on textured, safe surfaces strengthens ankle proprioceptors.
                2. **Assisted Stair Climbing:** Practice climbing low stairs under close supervision.
                3. **Push/Pull Toys:** Safe push-carts foster core stability and balance confidence.
            """.trimIndent()

            return PediatricAnalysisResult(
                answer = if (lang == "sw") swahili else english,
                isWarning = false,
                recommendedDoctor = "Pediatric Specialist",
                relatedMilestoneCategory = MilestoneCategory.MOTOR,
                relatedMilestoneId = "m_1_2_motor_walk",
                relatedMilestoneTitle = if (lang == "sw") "Kutembea Bila Kushikilia & Kusawazisha Mwili" else "Walking Independently & Postural Balance",
                developmentalTipSwahili = "Hakikisha sakafu haina utelezi na umruhusu atembee pekupeku kwa usalama.",
                developmentalTipEnglish = "Allow safe barefoot play indoors to enhance foot arch development.",
                suggestedObservationNote = "Anaweza kusimama na kupiga hatua kadhaa mbele kwa ujasiri."
            )
        }

        // 4. Digital screens / Simu / TV
        if (q.contains("simu") || q.contains("tv") || q.contains("skrini") || q.contains("screen") || q.contains("digital") || q.contains("youtube") || q.contains("tiktok")) {
            val swahili = """
                Mwongozo wa Kisasa wa Malezi ya Kidijitali kwa ${child.name}:
                
                📱 **Sayansi ya Skrini kwenye Ubongo wa Mtoto:**
                Katika umri wa miezi ${child.ageMonths}, ubongo unahitaji 'Dunia ya 3D'—kugusa vitu halisi, kudondosha, kuonja, na kuona sura za wanadamu halisi.
                
                💡 **Mbinu 4 za Kuokoa Mtoto Kutoka Kwenye Utegemezi wa Simu:**
                1. **Sheria ya Meza ya Chakula:** Hakuna simu wakati wa chakula. Mtoto anahitaji kuona rangi ya chakula na kutafuna polepole ili kuzuia matatizo ya mmeng'enyo.
                2. **Ubadilishaji Salama:** Badilisha video za YouTube na nyimbo za sauti (audio) au hadithi za redio.
                3. **Kikapu cha Vinyago vya Mikono:** Weka vinyago salama (building blocks, vyombo vya plastiki vya jikoni) anavyoweza kucheza navyo akiwa ameketi.
                4. **Mfano wa Mzazi:** Mtoto huiga anachokiona. Punguza muda unaotumia kushika simu ukiwa naye.
            """.trimIndent()

            val english = """
                Modern Digital Parenting Guide for ${child.name}:
                
                📱 **The Neuroscience of Screens:**
                At ${child.ageMonths} months, the brain wires through real tactile, multi-sensory experiences—gravity, texture, facial expressions, and joint play.
                
                💡 **Practical Digital Hygiene:**
                1. **No Screens During Meals:** Children need mindful sensory awareness of food textures and chewing reflexes.
                2. **Audio Over Video:** Use soothing children's stories or lullabies without rapid visual pacing.
                3. **Sensory Play Basket:** Provide stackable blocks and safe kitchen containers for tactile exploration.
                4. **Lead by Example:** Model phone-free playtime so the child sees engaged presence.
            """.trimIndent()

            return PediatricAnalysisResult(
                answer = if (lang == "sw") swahili else english,
                isWarning = false,
                recommendedDoctor = "Child Psychologist",
                relatedMilestoneCategory = MilestoneCategory.COGNITIVE,
                relatedMilestoneId = "m_1_2_cog_point",
                relatedMilestoneTitle = if (lang == "sw") "Kujenga Njia za Neva Bila Utegemezi wa Simu" else "Healthy Digital Hygiene & Tactile Play",
                developmentalTipSwahili = "Badilisha video za katuni kwa nyimbo za sauti au michezo ya mikono.",
                developmentalTipEnglish = "Substitute rapid cartoon videos with physical stacking toys or audio stories.",
                suggestedObservationNote = "Muda wa skrini umepunguzwa na ameanza kucheza zaidi na vinyago vya mikono."
            )
        }

        // 5. Food / Lishe / Dagaa / Nutrition
        if (q.contains("lishe") || q.contains("kula") || q.contains("chakula") || q.contains("food") || q.contains("dagaa") || q.contains("uzito") || q.contains("maziwa") || q.contains("nutrition")) {
            val swahili = """
                Mapendekezo ya Lishe ya Kuimarisha Ubongo kwa ${child.name} (${child.ageMonths} Miezi):
                
                🥗 **Mchanganyiko wa Dhahabu wa Afrika Mashariki:**
                1. **Omega-3 (DHA):** Unga wa dagaa wa ziwa (vijiko 1-2 kila siku kwenye mboga au uji) ni bora zaidi ya dawa za virutubisho vya madukani.
                2. **Choline:** Kiini cha yai moja la kienyeji lina kirutubisho kinachojenga kumbukumbu ya ubongo.
                3. **Vitamini A & Iron:** Supu ya viazi lishe vya njano, mchicha uliopikwa vizuri, na parachichi lililopondwa.
                
                🥣 **Kama Mtoto Anagoma Kula:**
                Usimlazimishe wala kumkaripia; mruhusu ashike chakula kwa vidole vyake (Finger Foods). Watoto hupenda uhuru wa kugusa na kujilisha.
            """.trimIndent()

            val english = """
                Nutritional Brain Architecture for ${child.name} (${child.ageMonths} Months):
                
                🥗 **East African Indigenous Superfoods:**
                1. **DHA & Omega-3:** Lake sardine powder (dagaa/omena) provides pure neuro-protective lipids.
                2. **Choline:** One boiled egg yolk provides essential memory neurotransmitter builders.
                3. **Iron & Carotenoids:** Mashed orange-fleshed sweet potato and local avocado.
                
                🥣 **Picky Eaters:**
                Avoid forced feeding or screen bribing. Offer colorful finger foods to empower self-regulation.
            """.trimIndent()

            return PediatricAnalysisResult(
                answer = if (lang == "sw") swahili else english,
                isWarning = false,
                recommendedDoctor = "Pediatric Nutritionist",
                relatedMilestoneCategory = MilestoneCategory.COGNITIVE,
                relatedMilestoneId = "m_10_12_motor_pincer",
                relatedMilestoneTitle = if (lang == "sw") "Kujilisha kwa Vidole (Finger Foods) & Lishe ya Ubongo" else "Self-Feeding Finger Foods & Brain Lipids",
                developmentalTipSwahili = "Mpe vipande vidogo vya ndizi au parachichi ale mwenyewe kwa vidole.",
                developmentalTipEnglish = "Offer soft finger foods like avocado cubes to build self-feeding independence.",
                suggestedObservationNote = "Anajifunza kujilisha vipande vya matunda na chakula chenye Omega-3."
            )
        }

        // 6. Tantrums / Hasira / Kulia / Usingizi
        if (q.contains("hasira") || q.contains("tantrum") || q.contains("kulia") || q.contains("usingizi") || q.contains("lala") || q.contains("sleep")) {
            val swahili = """
                Kuhusu Usingizi na Udhibiti wa Hisia kwa ${child.name}:
                
                🌙 **Mzunguko wa Usingizi wa Mtoto:**
                Watoto wa umri wa ${child.ageMonths} wanahitaji masaa 11 hadi 13 ya usingizi kwa siku. Mtoto anapokosa usingizi, mwili hutoa homoni ya msongo (Cortisol) inayomfanya awe mwenye hasira za ghafla mchana.
                
                ❤️ **Kukabiliana na Hasira (Tantrums):**
                - Mtoto anapolala chini akilia, ubongo wake wa hisia umefurika.
                - Usipige kelele wala kumchapa; piga magoti usawa wake na umwambie kwa sauti ya upole: "Nipo hapa kukulinda."
                - Mguse kwa upendo au mshike mkono. Akishatulia, ndipo mpe mbadala.
            """.trimIndent()

            val english = """
                Sleep Hygiene & Emotional Regulation for ${child.name}:
                
                🌙 **Sleep Needs:**
                Toddlers require 11 to 13 total hours of restorative sleep daily. Overtiredness triggers cortisol spikes resulting in day-time emotional meltdowns.
                
                ❤️ **Tantrum De-escalation:**
                - Meltdowns reflect an overwhelmed sensory circuit.
                - Lower your body to their eye level, speak in calm tones, and offer supportive physical comfort once safe.
            """.trimIndent()

            return PediatricAnalysisResult(
                answer = if (lang == "sw") swahili else english,
                isWarning = false,
                recommendedDoctor = "Child Psychologist",
                relatedMilestoneCategory = MilestoneCategory.EMOTIONAL,
                relatedMilestoneId = "m_1_2_social_empathy",
                relatedMilestoneTitle = if (lang == "sw") "Kudhibiti Hisia na Kupata Utulivu wa Usingizi" else "Emotional Regulation & Restful Sleep",
                developmentalTipSwahili = "Panga ratiba thabiti ya kulala kila siku bila skrini kabla ya usingizi.",
                developmentalTipEnglish = "Implement a predictable 30-minute screen-free bedtime wind-down.",
                suggestedObservationNote = "Anaonyesha utulivu mzuri mzazi anapomkumbatia wakati wa hasira."
            )
        }

        // Default holistic developmental guidance
        val swahiliDefault = """
            Asante kwa swali lako kuhusu malezi na afya ya mfumo wa fahamu ya ${child.name}!
            
            Kama msaidizi wako wa NeuroToto Insight AI:
            - **Ukuaji wa Neva na Hatua za Ukuaji (Growth Milestones):** Kila hatua ya mtoto inategemea muunganiko wa lishe bora ya asili, uhusiano wa upendo bila vitisho, na kumuwezesha kuchunguza mazingira yake halisi bila utegemezi wa skrini.
            - **Mada Maarufu za Ukuaji Unazoweza Kuniuliza:**
              1. Mtoto kuchelewa kuongea na mbinu za lugha
              2. Kudhibiti matumizi ya simu na televisheni nyumbani
              3. Vyakula vya asili vya kuongeza uwezo wa akili na kumbukumbu
              4. Miondoko ya misuli (kutembea, kukimbia, kusimama)
              5. Dalili za hatari (Red flags) za mfumo wa fahamu
            
            Kumbuka unaweza kurekodi hatua hii moja kwa moja hapa chini au kushauriana na madaktari wetu wataalamu!
        """.trimIndent()

        val englishDefault = """
            Thank you for your developmental question regarding ${child.name}'s neurodevelopmental progress!
            
            As your NeuroToto Insight AI Guide:
            - **Neural Architecture & Growth Milestones:** Early childhood brain development flourishes through face-to-face serve-and-return communication, indigenous brain nutrition, and healthy emotional safety.
            - **Key Developmental Topics You Can Ask:**
              1. Speech and language delay interventions
              2. Screen time detoxification and digital parenting
              3. Indigenous brain foods and feeding routines
              4. Gross & fine motor skill progress (walking, pincer grasp)
              5. Neurological red flags and milestone surveillance
            
            You can record and monitor this milestone directly below or consult verified pediatric specialists!
        """.trimIndent()

        return PediatricAnalysisResult(
            answer = if (lang == "sw") swahiliDefault else englishDefault,
            isWarning = false,
            recommendedDoctor = null,
            relatedMilestoneCategory = MilestoneCategory.COGNITIVE,
            relatedMilestoneId = "m_1_2_cog_point",
            relatedMilestoneTitle = if (lang == "sw") "Kufuatilia Hatua za Ukuaji wa Mtoto" else "Monitoring Child Growth Milestones",
            developmentalTipSwahili = "Fuatilia mabadiliko ya mtoto kila wiki na urekodi hatua anazofanikisha.",
            developmentalTipEnglish = "Track weekly developmental steps and celebrate achieved milestones.",
            suggestedObservationNote = "Anaendelea vizuri kulingana na miongozo ya ukuaji wa watoto."
        )
    }
}
