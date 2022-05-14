package uml;

import com.fasterxml.jackson.annotation.*;

public class UMLActivation{
    @JsonProperty("start")
    private Integer start;
    @JsonProperty("end")
    private Integer end;
    @JsonProperty("deactivation")
    private boolean deactivation = false;
    public UMLActivation(){}
    public UMLActivation(Integer start, Integer end)
    {
        this.start = start;
        this.end = end;
    }

    public UMLActivation(Integer start, Integer end, boolean deactivation)
    {
        this.start = start;
        this.end = end;
        this.deactivation = deactivation;
    }

    public Integer getStart(){
        return this.start;
    }
    public Integer getEnd(){
        return this.end;
    }

    public void setEnd(int newEnd)
    {
        this.end = newEnd;
    }

    public void setStart(int newStart)
    {
        if (newStart < 0)
        {
            this.start = 0;
            return;
        }

        this.start = newStart;
    }

    public boolean getDeactivation()
    {
        return this.deactivation;
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
