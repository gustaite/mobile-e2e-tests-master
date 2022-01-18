package util.values

enum class Action(val action: Visibility) {
    SELECT(Visibility.Visible),
    UNSELECT(Visibility.Invisible)
}
