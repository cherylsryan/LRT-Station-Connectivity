import java.util.Objects;

public class LRTStation {
    private String name;
    private String line;
    
    public LRTStation(String name, String line) {
        this.name = name;
        this.line = line;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLine() {
        return line;
    }
    
    public boolean isInterchange() {
        return name.equalsIgnoreCase("Masjid Jamek");
    }
    
    public String toString() {
        if (isInterchange()) {
            return "★ " + name + " (Interchange)";
        }
        return name + " (" + line + " Line)";
    }
    
    public String getDisplayName() {
        if (isInterchange()) {
            return "★ " + name;
        }
        return name;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LRTStation station = (LRTStation) o;
        return Objects.equals(name, station.name);
    }

    public int hashCode() {
        return Objects.hash(name);
    }
}
