package util.values

import commonUtil.asserts.VintedAssert
import util.Session

object Devices {

    fun getName(deviceUdid: String): String {
        val device = LaboratoryDevice.values().associateBy(LaboratoryDevice::udid).get(key = deviceUdid)
        return device?.deviceName ?: Session.sessionDetails.deviceModel
    }

    fun getUdid(deviceName: String): String {
        val device = LaboratoryDevice.values().associateBy(LaboratoryDevice::deviceName).get(key = deviceName)
        VintedAssert.assertNotNull(device?.udid, "Device $deviceName is not found on device name-udid mapping")
        return device!!.udid
    }
}

enum class LaboratoryDevice(val deviceName: String, val model: String, val udid: String) {
    // Samsung
    A22(deviceName = "A22", model = "SM_A225F", udid = "R58R6310QTJ"),
    A30s(deviceName = "A30s", model = "SM-A307FN", udid = "RF8MA118BJZ"),
    A31(deviceName = "A31", model = "SM-A315G", udid = "RF8N81MV7RB"),
    A41(deviceName = "A41", model = "SM-A415F", udid = "R58N50M2E5Y"),
    A50(deviceName = "A50", model = "SM-A505FN", udid = "R58M629D55A"),
    A50s(deviceName = "A50s", model = "SM-A507FN", udid = "RR8MA03E2AK"),
    A51(deviceName = "A51", model = "SM-A515F", udid = "R58MC3AXXVE"),
    A51_5G(deviceName = "A51 5G", model = "SM-A516B", udid = "R5CN601RAEA"),
    A52(deviceName = "A52", model = "SM-A525F", udid = "R58R32329NH"),
    A52_2(deviceName = "A52 2", model = "SM-A525F", udid = "R58R3231PSE"),
    A52_5G(deviceName = "A52 5G", model = "SM-A526B", udid = "R5CR30AQH0F"),
    A60(deviceName = "A60", model = "SM-A6060", udid = "R28M522C6AB"),
    A70(deviceName = "A70", model = "SM-A705FN", udid = "R58M875R0MJ"),
    A71(deviceName = "A71", model = "SM-A715F", udid = "R58N12L81JR"),
    A72(deviceName = "A72", model = "SM-A725F", udid = "R58R31F1GQA"),
    A72_2(deviceName = "A72 2", model = "SM-A725F", udid = "R58R31J4BHM"),
    A72_3(deviceName = "A72 3", model = "SM-A725F", udid = "R58R35BKLSR"),
    A80(deviceName = "A80", model = "SM-A805F", udid = "R58M6505TMK"),
    A90_5G(deviceName = "A90 5G", model = "SM-A908B", udid = "RFCM90GAKCV"),
    M30s(deviceName = "M30s", model = "SM-M307FN", udid = "RZ8MB09SS4E"),
    M31(deviceName = "M31", model = "SM-M315F", udid = "R58N41703AB"),
    M31s(deviceName = "M31s", model = "SM-M317F", udid = "R58N944ZTHF"),
    M51(deviceName = "M51", model = "SM-M515F", udid = "RF8N90QFB4W"),
    S6_EDGE(deviceName = "S6 Edge", model = "SM-G925F", udid = "06157df68aca4505"),
    S8(deviceName = "S8", model = "SM-G950F", udid = "ce031713e1e808040c"),
    S10(deviceName = "S10", model = "SM-G973F", udid = "RF8M20SCQCP"),
    S10e(deviceName = "S10e", model = "SM-G970F", udid = "RF8M82G1DNX"),
    S10_LITE(deviceName = "S10 lite", model = "SM-G770F", udid = "RF8N21A479J"),
    S10_PLUS(deviceName = "S10+", model = "SM-G975F", udid = "RF8MC20330R"),
    S20(deviceName = "S20", model = "SM-G980F", udid = "RF8N314ARRT"),
    S20_2(deviceName = "S20 2", model = "SM-G980F", udid = "RF8N505MGBA"),
    S20_FE(deviceName = "S20 FE", model = "SM-G780F", udid = "RF8N91PJFKM"),
    S20_FE_2(deviceName = "S20 FE 2", model = "SM-G780F", udid = "RF8N91M1STJ"),
    S20_FE_3(deviceName = "S20 FE 3", model = "SM-G780F", udid = "RF8N91G4FVX"),
    S20_PLUS(deviceName = "S20+", model = "SM-G986B", udid = "RFCN300PVXH"),
    S20_PLUS_2(deviceName = "S20+ 2", model = "SM-G986B", udid = "R5CN206ADPA"),
    S21(deviceName = "S21", model = "SM-G991B", udid = "R5CR120732D"),
    S21_2(deviceName = "S21 2", model = "SM-G991B", udid = "RFCR10L4LAW"),
    S21_3(deviceName = "S21 3", model = "SM-G991B", udid = "R5CR120786V"),
    S21_4(deviceName = "S21 4", model = "SM-G991B", udid = "RFCR10L47ZK"),
    S21_5(deviceName = "S21 5", model = "SM-G991B", udid = "R5CR1207A5H"),
    S21_6(deviceName = "S21 6", model = "SM-G991B", udid = "R5CR1207CZP"),
    S21_7(deviceName = "S21 7", model = "SM-G991B", udid = "R5CR102KJ9T"),
    S21_8(deviceName = "S21 8", model = "SM-G991B", udid = "R5CR105X6QW"),
    S21_9(deviceName = "S21 9", model = "SM_G991B", udid = "R5CRA02J51R"),
    S21_ULTRA(deviceName = "S21 Ultra", model = "SM-G998B", udid = "R5CNC1B65JM"),
    S21_PLUS_5G(deviceName = "S21+ 5G", model = "SM-G996B", udid = "RFCR20J26KA"),
    S21_PLUS_5G_2(deviceName = "S21+ 5G", model = "SM-G996B", udid = "RFCNC1271ZE"),
    GALAXY_NOTE_9(deviceName = "Galaxy Note 9", model = "SM-N960F", udid = "25a321a540027ece"),
    GALAXY_NOTE_9_2(deviceName = "Galaxy Note 9 2", model = "SM-N960F", udid = "2a9b694128037ece"),
    GALAXY_NOTE_10(deviceName = "Galaxy Note 10", model = "SM-N970F", udid = "RZ8MB2K7DRV"),
    GALAXY_NOTE_10_PLUS(deviceName = "Galaxy Note 10+", model = "SM-N975F", udid = "RF8M90TXKPR"),
    GALAXY_NOTE_10_LITE(deviceName = "Galaxy Note 10 Lite", model = "SM-N770F", udid = "RF8N11PPHZB"),
    GALAXY_NOTE_20(deviceName = "Galaxy Note 20", model = "SM-N980F", udid = "RF8N8161P9H"),
    GALAXY_NOTE_20_2(deviceName = "Galaxy Note 20 2", model = "SM-N980F", udid = "RF8NC1GA5AY"),
    XCOVER_PRO(deviceName = "Xcover Pro", model = "SM-G715FN", udid = "R58N12BLQ2L"),
    Z_FLIP3(deviceName = "Galaxy Z Flip3", model = "SM_F711B", udid = "R5CR80P4GZD"),
    // Sony
    XPERIA_XZ3(deviceName = "Xperia XZ3", model = "H9436", udid = "BH9301JPEC"),
    XPERIA_5(deviceName = "Xperia 5", model = "J9210", udid = "QV710X2C24"),
    XPERIA_5_2(deviceName = "Xperia 5 II", model = "XQ-AT51", udid = "QV7134LQ3F"), // not the same model as Xperia 5
    // LG
    LG_G8_THIN_Q(deviceName = "LG G8 ThinQ", model = "LM-G850", udid = "LMG850EMW34d9d1c3"),
    LG_VELVET_5G(deviceName = "LG Velvet 5G", model = "LM-G900", udid = "LMG900EM6434bb02"),
    // 1+
    ONE_PLUS_8(deviceName = "One Plus 8", model = "IN2013", udid = "d24f1012"),
    ONE_PLUS_8_PRO(deviceName = "One Plus 8 Pro", model = "IN2023", udid = "1b305b89"),
    ONE_PLUS_7_PRO(deviceName = "One Plus 7 Pro", model = "GM1913", udid = "b5127173"),
    ONE_PLUS_7T_PRO(deviceName = "One Plus 7T Pro", model = "HD1913", udid = "7a7909fa"),
    ONE_PLUS_8T(deviceName = "One Plus 8T", model = "KB2003", udid = "5569fde3"),
    ONE_PLUS_9(deviceName = "One Plus 9", model = "OnePlus9_EEA", udid = "daf5aaa5"),
    ONE_PLUS_NORD_5G(deviceName = "One Plus Nord 5G", model = "AC2003", udid = "3d72661e"),
    ONE_PLUS_NORD_N10_5G(deviceName = "One Plus Nord N10 5G", model = "BE2029", udid = "de35c0e3"),
    // Google
    PIXEL_2(deviceName = "Pixel 2", model = "Pixel 2", udid = "FA83Y1A01025"),
    PIXEL_3_XL(deviceName = "Pixel 3 XL", model = "Pixel 3 XL", udid = "8ACY0HM3X"),
    PIXEL_3_XL_2(deviceName = "Pixel 3 XL 2", model = "Pixel 3 XL", udid = "89BY05CN7"),
    PIXEL_4_XL(deviceName = "Pixel 4 XL", model = "Pixel 4 XL", udid = "9B301FFBA003E2"),
    PIXEL_5(deviceName = "Pixel 5", model = "Pixel 5", udid = "0C211FDD40037A"),
    PIXEL_5_2(deviceName = "Pixel 5 2", model = "Pixel 5", udid = "0C211FDD40035B"),
    PIXEL_6(deviceName = "Pixel 6", model = "Pixel 6", udid = "18231FDF6004W6"),
    // Xiaomi
    REDMI_NOTE_8_PRO(deviceName = "Redmi Note 8 Pro", model = "Redmi Note 8 Pro", udid = "twvwvwc6cyhetslv"),
    REDMI_NOTE_9_PRO(deviceName = "Redmi Note 9 Pro", model = "Redmi Note 9 Pro", udid = "f3a77995"),
    REDMI_NOTE_9S(deviceName = "Redmi Note 9s", model = "Redmi Note 9s", udid = "47ffbda9"),
    MI_NOTE_10(deviceName = "Mi Note 10", model = "Mi Note 10", udid = "cf532761"),
    MI_10(deviceName = "Mi 10", model = "Mi 10", udid = "3d033e62"),
    MI_10_LITE_5G(deviceName = "Mi 10 Lite 5G", model = "M2002J9G", udid = "f4b3ee8a"),
    XIAOMI_11T_PRO(deviceName = "11T Pro", model = "2107113SG", udid = "4555537d"),
    // Motorola
    MOTOROLA_ONE_ZOOM(deviceName = "Motorola One Zoom", model = "ZY226L42V7", udid = "ZY226L42V7"),
    MOTOROLA_ONE_EDGE(deviceName = "Motorola One Edge", model = "motorola edge", udid = "ZY227HVNM9"),
    // Nokia
    NOKIA_53(deviceName = "Nokia 5.3", model = "Nokia 5.3", udid = "N0AA003689K71500195")
}
