package sk.adresa.eaukcia.core.data;

public class Constant<T> {
    private T id;
    private String description; 
    
    public Constant() {
    }
    
    public Constant(T id) {
      this.id = id;
    }
    
    public Constant(T id,String description) {
      this.id = id;
      this.description = description;
    }
    
    public void setId(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return 
        "\nObject Constant:{" + 
        "\n  id: " + id + 
        "\n  description: " + description   + 
        "\n}";

    }
}
