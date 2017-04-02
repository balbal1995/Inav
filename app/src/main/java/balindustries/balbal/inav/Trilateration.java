package balindustries.balbal.inav;

/**
 * Created by lenovo on 5/17/2016.
 */
public class Trilateration {
    double  jarak1,
            jarak2,
            jarak3,
            t=1.5,  //1,5 meter
            A,
            B,
            C,
            X13,
            X21,
            X32,
            Y13,
            Y21,
            Y32,
            x1,
            x2,
            x3,
            y1,
            y2,
            y3,
            x,
            y;

    public Trilateration(double x1, double y1, double x2, double y2, double x3, double y3, double jarak1, double jarak2, double jarak3){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.jarak1 = Math.sqrt(Math.pow(jarak1,2)-Math.pow(t,2))*15;
        this.jarak2 = Math.sqrt(Math.pow(jarak2,2)-Math.pow(t,2))*15;
        this.jarak3 = Math.sqrt(Math.pow(jarak3,2)-Math.pow(t,2))*15;


    }

    public void itung(){
        X13 = x1 - x3;
        X21 = x2 - x1;
        X32 = x3 - x2;

        Y13 = y1 - y3;
        Y21 = y2 - y1;
        Y32 = y3 - y2;

        A = Math.pow(x1,2) + Math.pow(y1,2) - Math.pow(jarak1,2);
        B = Math.pow(x2,2) + Math.pow(y2,2) - Math.pow(jarak2,2);
        C = Math.pow(x3,2) + Math.pow(y3,2) - Math.pow(jarak3,2);
    }

    public double getX(){
        x = (A*Y32 + B*Y13 + C*Y21)/(2*(x1*Y32 + x2*Y13 + x3*Y21));
        return x;
    }

    public double getY(){
        y = (A*X32 + B*X13 + C*X21)/(2*(y1*X32 + y2*X13 + y3*X21));
        return y;
    }
}
