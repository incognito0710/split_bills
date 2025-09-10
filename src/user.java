public class user {
    int id;
    String name;

    public user(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name; // so comboBox shows name instead of object ref
    }
}
