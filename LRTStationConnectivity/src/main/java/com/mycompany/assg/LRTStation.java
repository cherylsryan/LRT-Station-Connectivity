/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.assg;

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
        return name + " [INTERCHANGE]";
    }
    return name + " (" + line + " Line)";
}
    
    public String getDisplayName() {
    if (isInterchange()) {
        return name + " [INTERCHANGE]";
    }
    return name;
}

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LRTStation station = (LRTStation) o;
        return name != null && name.equalsIgnoreCase(station.name);
    }

    public int hashCode() {
        return Objects.hash(name == null ? null : name.toLowerCase());
    }
}