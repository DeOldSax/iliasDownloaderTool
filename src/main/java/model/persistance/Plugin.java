package model.persistance;

public class Plugin implements Storable {

    public Plugin() {

    }

    public Plugin(String name, String display) {
        this.name = name;
        this.display = display;
    }

    private static final long serialVersionUID = 6880157243670452604L;
    private String name;
    private String display;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   public String getDisplay() {
        return display;
   }

   public void setDisplay(String display) {
        this.display = display;
   }

    @Override
    public String getStorageFileName() {
        return "plugin.ser";
    }
}
