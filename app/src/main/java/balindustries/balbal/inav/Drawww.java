package balindustries.balbal.inav;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by lenovo on 2/24/2016.
 */
public class Drawww extends ImageView{

    Bitmap pointer, map, rotatep1, rotatep2;
    public static int x, y;
    int r;
    float r1, r2, r3, r4, r5, r6;
    public static ArrayList<Point> route;

    private static final String TAG = "Touch";
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    Matrix savedMatrix2 = new Matrix();

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;



    int imleft = 0, imtop = 0;
    public static boolean setstra = false, nav = false, viewnode = false, view_AP = false;

    PointF start = new PointF();
    PointF mid = new PointF();
    PointF[] nv = { new PointF(123f,585f),
            new PointF(123f,530f),
            new PointF(123f,440f),
            new PointF(123f,355f),
            new PointF(123f,270f),
            new PointF(123f,210f),
            new PointF(148f,185f),
            new PointF(295f,185f),
            new PointF(360f,185f),
            new PointF(505f,185f),
            new PointF(535f,215f),
            new PointF(535f,265f),
            new PointF(535f,350f),
            new PointF(535f,445f),
            new PointF(535f,530f),
            new PointF(535f,585f),
            new PointF(100f,625f),  //lantai
            new PointF(100f,85f),   //lantai
            new PointF(560f,85f),   //lantai
            new PointF(560f,625f),  //lantai
            new PointF(100f,155f),  //toilet
            new PointF(560f,160f)}; //toilet
    float oldDist = 1f, d, newRot, lastEvent[], olddegree;
    float postx, posty, oldx, oldy;
    public static int lastTouchX = 5, lastTouchY = 5;
    Paint p, p1, b, b1, c, c1, red, red1, blu, blu1, bla, bla1, tp, tb, tc, tr, tbu, tba, line,g;
    Path path;

    public static ArrayList<Node> nodee = new ArrayList<>();
    public static ArrayList<Node> trinodel = new ArrayList<>();

    public static Node  node1 = new Node(140,490),
            node2 = new Node(140,285),
            node3 = new Node(235,200),
            node4 = new Node(430,200),
            node5 = new Node(528,320),
            node6 = new Node(528,445);


    public Drawww(Context context){
        super(context);

//        if (!isInEditMode()) {
            init(context);
//        }
    }

    public Drawww(Context context, AttributeSet attrs){
        super(context, attrs);

//        if (!isInEditMode()) {
            init(context);
//        }
    }

    public Drawww(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (!isInEditMode()) {
            init(context);
        }
    }

    public void init(Context context){

        map = BitmapFactory.decodeResource(getResources(), R.drawable.lantai22);
        pointer = decodeSampledBitmapFromResource(getResources(), R.drawable.pointer, 20, 20);

        Matrix matrixp = new Matrix();
        matrixp.postRotate(90);
        rotatep1 = Bitmap.createBitmap(pointer , 0, 0, pointer.getWidth(), pointer.getHeight(), matrixp, true);

        matrixp = new Matrix();
        matrixp.postRotate(180);
        rotatep2 = Bitmap.createBitmap(pointer , 0, 0, pointer.getWidth(), pointer.getHeight(), matrixp, true);

        nodee = new ArrayList<>();

        node1.addNeighbor(node2);
        node1.addNeighbor(node3);
        node2.addNeighbor(node3);
        node3.addNeighbor(node4);
        node4.addNeighbor(node5);
        node5.addNeighbor(node6);
        node4.addNeighbor(node6);

        nodee.add(node1);
        nodee.add(node2);
        nodee.add(node3);
        nodee.add(node4);
//        nodee.add(node5);
//        nodee.add(node6);

        path = new Path();

        line = new Paint();
        line.setAntiAlias(true);
        line.setColor(Color.RED);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(3);
        line.setStrokeCap(Paint.Cap.ROUND);
        line.setPathEffect(null);

        g = new Paint();
        g.setColor(Color.MAGENTA);
        g.setStyle(Paint.Style.FILL_AND_STROKE);
        g.setStrokeWidth(1);

        p = new Paint();
        p.setColor(Color.MAGENTA);
        p.setStyle(Paint.Style.FILL);

        p1 = new Paint();
        p1.setColor(Color.MAGENTA);
        p1.setStyle(Paint.Style.STROKE);

        tp = new Paint();
        tp.setColor(Color.MAGENTA);
        tp.setTextSize(16);

        b = new Paint();
        b.setColor(Color.CYAN);
        b.setStyle(Paint.Style.FILL);

        b1 = new Paint();
        b1.setColor(Color.CYAN);
        b1.setStyle(Paint.Style.STROKE);

        tb = new Paint();
        tb.setColor(Color.CYAN);
        tb.setTextSize(16);

        c = new Paint();
        c.setColor(Color.YELLOW);
        c.setStyle(Paint.Style.FILL);

        c1 = new Paint();
        c1.setColor(Color.YELLOW);
        c1.setStyle(Paint.Style.STROKE);

        tc = new Paint();
        tc.setColor(Color.YELLOW);
        tc.setTextSize(16);

        red = new Paint();
        red.setColor(Color.RED);
        red.setStyle(Paint.Style.FILL);

        red1 = new Paint();
        red1.setColor(Color.RED);
        red1.setStyle(Paint.Style.STROKE);

        tr = new Paint();
        tr.setColor(Color.RED);
        tr.setTextSize(16);

        blu = new Paint();
        blu.setColor(Color.BLUE);
        blu.setStyle(Paint.Style.FILL);

        blu1 = new Paint();
        blu1.setColor(Color.BLUE);
        blu1.setStyle(Paint.Style.STROKE);

        tbu = new Paint();
        tbu.setColor(Color.BLUE);
        tbu.setTextSize(16);

        bla = new Paint();
        bla.setColor(Color.BLACK);
        bla.setStyle(Paint.Style.FILL);

        bla1 = new Paint();
        bla1.setColor(Color.BLACK);
        bla1.setStyle(Paint.Style.STROKE);

        tba = new Paint();
        tba.setColor(Color.BLACK);
        tba.setTextSize(16);

        x = 5;
        y = 5;

        r = 10;

        olddegree = 0;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float scale;

        if (setstra) {
            coorImagen(event);
        }
        // log event
//        logEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                oldx = event.getX();
                oldy = event.getY();
                start.set(oldx, oldy);
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
//                Log.d(TAG, "mode=NONE");
                break;


            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);

                    postx = event.getX() - oldx;
                    posty = event.getY() - oldy;

                    oldx = event.getX();
                    oldy = event.getY();

                } else if (mode == ZOOM && event.getPointerCount() == 2) {
                    float newDist = spacing(event);
                    matrix.set(savedMatrix);
                    if (newDist > 10f) {
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null) {
                        newRot = rotation(event);
                        float r = newRot - d;
                    }
                }
                break;

        }
        if ((mode == DRAG) || mode == ZOOM) {
            imleft += (int) postx;
            imtop += (int) posty;

            invalidate();
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();


        canvas.setMatrix(matrix);

        canvas.drawBitmap(map, 0, 0, null);


        if (viewnode) {
            for (int i = 0; i <=21; i++) {
                if (i<16) {
                    canvas.drawCircle(nv[i].x, nv[i].y, r, blu);
                } else if (i<20){
                    canvas.drawCircle(nv[i].x, nv[i].y, r, p);
                } else {
                    canvas.drawCircle(nv[i].x, nv[i].y, r, c);
                }
            }
        }

        if (view_AP) {
            r1 = (float) nodee.get(0).jarak;
            r2 = (float) nodee.get(1).jarak;
            r3 = (float) nodee.get(2).jarak;
            r4 = (float) nodee.get(3).jarak;
//            r5 = (float) nodee.get(4).jarak;
//            r6 = (float) nodee.get(5).jarak;

            String jar1 = Float.toString(r1) + " m";
            String jar2 = Float.toString(r2) + " m";
            String jar3 = Float.toString(r3) + " m";
            String jar4 = Float.toString(r4) + " m";
//            String jar5 = Float.toString(r5) + " m";
//            String jar6 = Float.toString(r6) + " m";


            canvas.drawCircle(nodee.get(0).x, nodee.get(0).y, r, red);
            canvas.drawCircle(nodee.get(0).x, nodee.get(0).y, r1 * 15, red1);
            canvas.drawText(jar1, nodee.get(0).x, nodee.get(0).y + 15 + (r1 * 10), tr);

            canvas.drawCircle(nodee.get(1).x, nodee.get(1).y, r, red);
            canvas.drawCircle(nodee.get(1).x, nodee.get(1).y, r2 * 15, red1);
            canvas.drawText(jar2, nodee.get(1).x, nodee.get(1).y + 15 + (r2 * 10), tr);

            canvas.drawCircle(nodee.get(2).x, nodee.get(2).y, r, red);
            canvas.drawCircle(nodee.get(2).x, nodee.get(2).y, r3 * 15, red1);
            canvas.drawText(jar3, nodee.get(2).x, nodee.get(2).y + 15 + (r3 * 10), tr);

            canvas.drawCircle(nodee.get(3).x, nodee.get(3).y, r, red);
            canvas.drawCircle(nodee.get(3).x, nodee.get(3).y, r4 * 15, red1);
            canvas.drawText(jar4, nodee.get(3).x, nodee.get(3).y + 15 + (r4 * 10), tr);

//            canvas.drawCircle(nodee.get(4).x, nodee.get(4).y, r, red);
//            canvas.drawCircle(nodee.get(4).x, nodee.get(4).y, r5 * 15, red1);
//            canvas.drawText(jar5, nodee.get(4).x, nodee.get(4).y + 15 + (r5 * 10), tr);
//
//            canvas.drawCircle(nodee.get(5).x, nodee.get(5).y, r, red);
//            canvas.drawCircle(nodee.get(5).x, nodee.get(5).y, r6 * 10, red1);
//            canvas.drawText(jar6, nodee.get(5).x, nodee.get(5).y + 15 + (r6 * 10), tr);
        }

        if (nav) {
            path = new Path();
            for (int i=0;i<route.size();i++) {
                if (i==0) {
                    path.moveTo(route.get(0).x,route.get(0).y);
//                    Log.d(ROUTE,"route = (" +route.get(0).x + "," + route.get(0).y + ")");
                } else {
                    path.lineTo(route.get(i).x,route.get(i).y);
//                    Log.d(ROUTE,"route = (" +route.get(i).x + "," + route.get(i).y + ")");
                }
            }
            canvas.drawPath(path,line);
        }

        if (x == 123) {
            canvas.drawBitmap(pointer, x - pointer.getWidth() / 2, y - pointer.getHeight() / 2, null);
        } else if (y == 185){
            canvas.drawBitmap(rotatep1, x - rotatep1.getWidth() / 2, y - rotatep1.getHeight() / 2, null);
        } else if (x == 535){
            canvas.drawBitmap(rotatep2, x - rotatep2.getWidth() / 2, y - rotatep2.getHeight() / 2, null);
        }

        canvas.restore();

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public void refresh(){
        invalidate();
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);

        return (float) Math.toDegrees(radians);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);

    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);

    }


    void coorImagen(MotionEvent e){
        float []m = new float[9];
        matrix.getValues(m);
        float transX = m[Matrix.MTRANS_X] * -1;
        float transY = m[Matrix.MTRANS_Y] * -1;
        float scaleX = m[Matrix.MSCALE_X];
        float scaleY = m[Matrix.MSCALE_Y];
        lastTouchX = (int) ((e.getX() + transX) / scaleX);
        lastTouchY = (int) ((e.getY() + transY) / scaleY);
        lastTouchX = Math.abs(lastTouchX) + 20;
        lastTouchY = Math.abs(lastTouchY) + 100;

//        Log.d(MAP,"coordinate = (" +lastTouchX + "," + lastTouchY + ")");
    }

    public static Point setstart(){
        return new Point(lastTouchX,lastTouchY);
    }

    public static class Node {
        int y;
        int x;
        double jarak;
        String SSID;

        public Node(int x, int y){
            this.x = x;
            this.y = y;
        }

        ArrayList<Node> neighbors = new ArrayList<>();

        void addNeighbor(Node other) {
            if (!this.neighbors.contains(other)) {
                this.neighbors.add(other);
            }
            if (!other.neighbors.contains(this)) {
                other.neighbors.add(this);
            }
        }

    }

    public static void get3lowestnode(){
        trinodel = new ArrayList<>();
        Node  cnode1 = node1,
                cnode2 = node2,
                cnode3 = node3,
                cnode4 = node4,
                cnode5 = node5,
                cnode6 = node6;

        trinodel.add(cnode1);
        trinodel.add(cnode2);
        trinodel.add(cnode3);
        trinodel.add(cnode4);
//        trinodel.add(cnode5);
//        trinodel.add(cnode6);

        Collections.sort(trinodel,new NodeComparator());
    }

    private static class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node node1, Node node2){
            return (int) node1.jarak - (int) node2.jarak;
        }
    }

    private void logEvent(final MotionEvent event) {
                String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                        "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
                StringBuilder sb = new StringBuilder();
                int action = event.getAction();
                int actionCode = action & MotionEvent.ACTION_MASK;
                sb.append("event ACTION_").append(names[actionCode]);
                if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                        || actionCode == MotionEvent.ACTION_POINTER_UP) {
                    sb.append("(pid ").append(
                            action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                    sb.append(")");
                }

                sb.append("[");

                for (int i = 0; i < event.getPointerCount(); i++) {
                    sb.append("#").append(i);
                    sb.append("(pid ").append(event.getPointerId(i));
                    sb.append(")=").append((int) event.getX(i));
                    sb.append(",").append((int) event.getY(i));
                    if (i + 1 < event.getPointerCount())

                        sb.append(";");
                }

                sb.append("]");
                Log.d(TAG, sb.toString());

            }
}
