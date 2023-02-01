package bo.NewClasses;

public class NameValueInt {
    public String name;
    public Integer value;
    public String other;

    public NameValueInt(){

    }
    public NameValueInt( Integer val, String name, String other){
        this.name = name;
        this.value = val;
        this.other = other;
    }
    public NameValueInt( Integer val, String name){
        this.name = name;
        this.value = val;
        this.other = "";
    }
}
