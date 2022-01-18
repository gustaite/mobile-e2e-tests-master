package util.values

enum class StevesMap(val endOfIP: String, val zeroTier: String) {
    STEVE_1("16.16", "vnc://192.168.192.33"),
    STEVE_2("16.19", "vnc://192.168.192.94"),
    STEVE_3("16.10", "vnc://192.168.192.129"),
    STEVE_4("16.17", "vnc://192.168.192.111"),
    STEVE_5("16.12", "vnc://192.168.192.43"),
    STEVE_6("16.15", "vnc://192.168.192.55"),
    STEVE_7("16.18", "vnc://192.168.192.171"),
    STEVE_8("16.13", "vnc://192.168.192.218"),
    STEVE_9("16.11", "vnc://192.168.192.245"),
    STEVE_10("16.21", "vnc://192.168.192.103"),
    STEVE_11("16.20", "vnc://192.168.192.20"),
    STEVE_12("16.24", "vnc://192.168.192.113"),
    STEVE_13("16.25", "vnc://192.168.192.138"),
    STEVE_14("16.26", "vnc://192.168.192.39"),
    STEVE_15("16.29", "vnc://192.168.192.243"),
    STEVE_16("16.30", "vnc://192.168.192.58"),
    STEVE_17("16.31", "vnc://192.168.192.229"),
    STEVE_18("16.32", "vnc://192.168.192.98"),
    STEVE_19("16.33", "vnc://192.168.192.3"),
    STEVE_20("16.34", "vnc://192.168.192.211");

    companion object {
        fun getSteve(host: String): StevesMap? {
            return values().find { steve -> host.endsWith(steve.endOfIP) }
        }
    }
}
