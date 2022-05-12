package uml;

import com.fasterxml.jackson.annotation.*;

public class UMLActivation{
    @JsonProperty("start")
    private Integer start;

    @JsonProperty("end")
    private Integer  end;
    public UMLActivation(){}
    public UMLActivation(Integer  start, Integer  end)
    {
        this.start = start;
        this.end = end;
    }
    public Integer getStart(){
        return this.start;
    }
    public Integer getEnd(){
        return this.end;
    }

    public boolean isInBounds(int number)
    {
        if (this.start <= number && number <= this.end)
        {
            return true;
        }
        return false;
        
    }
}
