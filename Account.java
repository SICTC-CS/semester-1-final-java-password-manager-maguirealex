public class Account {
    //field variable or global variables
    private String name, username, password, category;

    
    //Constructor - builds an object
    public Account(String name, String username, String password, String category){
        //this is to allow java to connect the name variable to the object we call in 
        //  other files. Without this, Java will not understand what var you're accessing
        this.name = name;
        this.username = username;
        this.password = password;
        this.category = category;
        // System.out.println(newName);
        // System.out.println(this.name+" was created!");
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    

    //technically you should have your no-arg Constructor
    public Account(){}

    //getters and setters

    //extra f(x)

    @Override
    public String toString() {
        return "Account \nname=" + this.name + "\nusername=" + this.username + "\npassword=" + this.password + "\ncategory=" + this.category;
    }
}
