package balindustries.balbal.inav;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by lenovo on 4/19/2016.
 */
public class AStar {

    boolean found;
    boolean searching;
    boolean endOfSearch;
    boolean findstart;
    int expanded;

    Cell robotStart;
    Cell targetPos;
    public  ArrayList<Point> route;

    int rows, columns;

    ArrayList<Cell> openSet   = new ArrayList();
    ArrayList<Cell> closedSet = new ArrayList();
    ArrayList<Cell> tangga = new ArrayList<>();
    ArrayList<Cell> toilet = new ArrayList<>();

    public static ArrayList<Cell> node = new ArrayList<>();
    Cell node1 = new Cell(123,585);     //N201
    Cell node2 = new Cell(123,530);     //N202
    Cell node3 = new Cell(123,440);     //N203
    Cell node4 = new Cell(123,355);     //N204
    Cell node5 = new Cell(123,270);     //N205
    Cell node6 = new Cell(123,210);     //N206
    Cell node7 = new Cell(148,185);     //N207
    Cell node8 = new Cell(295,185);     //N208
    Cell node9 = new Cell(360,185);     //N209
    Cell node10 = new Cell(505,185);    //N210
    Cell node11 = new Cell(535,215);    //N211
    Cell node12 = new Cell(535,265);    //N212
    Cell node13 = new Cell(535,350);    //N213
    Cell node14 = new Cell(535,445);    //N214
    Cell node15 = new Cell(535,530);    //N215
    Cell node16 = new Cell(535,585);    //N216
    Cell node17 = new Cell(100,625);    //tangga kiri bawah
    Cell node18 = new Cell(100,85);     //tangga kiri atas
    Cell node19 = new Cell(560,85);     //tangga kanan atas
    Cell node20 = new Cell(560,625);    //tangga kanan bawah
    Cell node21 = new Cell(100,155);    //toilet kiri
    Cell node22 = new Cell(560,160);    //toilet kanan

    public AStar(){
        node = new ArrayList<>();
        node1.addNeighbor(node2);
        node1.addNeighbor(node17);
        node2.addNeighbor(node3);
        node3.addNeighbor(node4);
        node4.addNeighbor(node5);
        node5.addNeighbor(node6);
        node6.addNeighbor(node7);
        node6.addNeighbor(node21);
        node21.addNeighbor(node18);
        node7.addNeighbor(node8);
        node7.addNeighbor(node21);
        node8.addNeighbor(node9);
        node9.addNeighbor(node10);
        node10.addNeighbor(node11);
        node10.addNeighbor(node22);
        node22.addNeighbor(node19);
        node11.addNeighbor(node22);
        node11.addNeighbor(node12);
        node12.addNeighbor(node13);
        node13.addNeighbor(node14);
        node14.addNeighbor(node15);
        node15.addNeighbor(node16);
        node16.addNeighbor(node20);

        node.add(node1);
        node.add(node2);
        node.add(node3);
        node.add(node4);
        node.add(node5);
        node.add(node6);
        node.add(node7);
        node.add(node8);
        node.add(node9);
        node.add(node10);
        node.add(node11);
        node.add(node12);
        node.add(node13);
        node.add(node14);
        node.add(node15);
        node.add(node16);
        node.add(node17);
        node.add(node18);
        node.add(node19);
        node.add(node20);
        node.add(node21);
        node.add(node22);
    }

    public AStar(int widthmaze, int heigtmaze, int startx, int starty, int targetx, int targety){
        this.columns = widthmaze;
        this.rows    = heigtmaze;

        node = new ArrayList<>();
        targetPos = new Cell(targetx, targety);
        robotStart = new Cell(startx, starty);

        openSet.removeAll(openSet);
        openSet.add(robotStart);
        closedSet.removeAll(closedSet);

        node1.addNeighbor(node2);
        node1.addNeighbor(node17);
        node2.addNeighbor(node3);
        node3.addNeighbor(node4);
        node4.addNeighbor(node5);
        node5.addNeighbor(node6);
        node6.addNeighbor(node7);
        node6.addNeighbor(node21);
        node21.addNeighbor(node18);
        node7.addNeighbor(node8);
        node7.addNeighbor(node21);
        node8.addNeighbor(node9);
        node9.addNeighbor(node10);
        node10.addNeighbor(node11);
        node10.addNeighbor(node22);
        node22.addNeighbor(node19);
        node11.addNeighbor(node22);
        node11.addNeighbor(node12);
        node12.addNeighbor(node13);
        node13.addNeighbor(node14);
        node14.addNeighbor(node15);
        node15.addNeighbor(node16);
        node16.addNeighbor(node20);

        node.add(node1);
        node.add(node2);
        node.add(node3);
        node.add(node4);
        node.add(node5);
        node.add(node6);
        node.add(node7);
        node.add(node8);
        node.add(node9);
        node.add(node10);
        node.add(node11);
        node.add(node12);
        node.add(node13);
        node.add(node14);
        node.add(node15);
        node.add(node16);
        node.add(node17);
        node.add(node18);
        node.add(node19);
        node.add(node20);
        node.add(node21);
        node.add(node22);

        tangga.add(node17);
        tangga.add(node18);
        tangga.add(node19);
        tangga.add(node20);
        toilet.add(node21);
        toilet.add(node22);


        int ind = isInList(node,targetPos);
        if (ind == 16){
            Collections.sort(tangga,new CellComparatorByDist());
            targetPos = tangga.get(0);
        } else if (ind == 20){
            Collections.sort(toilet,new CellComparatorByDist());
            targetPos = toilet.get(0);
        }
    }


    public static class Cell {
        int y;
        int x;
        int g;
        int h;
        int f;
        Cell prev;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        ArrayList<Cell> neighbors = new ArrayList<>();

        void addNeighbor(Cell other) {
            if (!this.neighbors.contains(other)) {
                this.neighbors.add(other);
            }
            if (!other.neighbors.contains(this)) {
                other.neighbors.add(this);
            }
        }
    }


    private class CellComparatorByF implements Comparator<Cell> {
        @Override
        public int compare(Cell cell1, Cell cell2){
            return cell1.f-cell2.f;
        }
    }

    private class CellComparatorByG implements Comparator<Cell> {
        @Override
        public int compare(Cell cell1, Cell cell2){
            return cell1.g-cell2.g;
        }
    }

    private class CellComparatorByDist implements Comparator<Cell>{
        @Override
        public int compare(Cell cell1, Cell cell2){
            int dxg1 = robotStart.x-cell1.x;
            int dyg1 = robotStart.y-cell1.y;

            int dxg2 = robotStart.x-cell2.x;
            int dyg2 = robotStart.y-cell2.y;

            int g1 = (int)((double)1000*Math.sqrt(dxg1*dxg1 + dyg1*dyg1));

            int g2 = (int)((double)1000*Math.sqrt(dxg2*dxg2 + dyg2*dyg2));

            return g1-g2;
        }
    }

    private void expandNode(){
        Cell current;

        Collections.sort(openSet, new CellComparatorByF());
        current = openSet.remove(0);

        closedSet.add(0,current);

        if (current.x == targetPos.x && current.y == targetPos.y) {
            Cell last = targetPos;
            last.prev = current.prev;
            closedSet.add(last);
            found = true;
            return;
        }

        expanded++;

        ArrayList<Cell> succesors;
        succesors = createSuccesors(current);

        for (int i = 0; i<succesors.size();i++){
            Cell cell = succesors.get(i);
            int dxg = current.x-cell.x;
            int dyg = current.y-cell.y;
            int dxh = targetPos.x-cell.x;
            int dyh = targetPos.y-cell.y;

            cell.g = current.g+(int)((double)1000*Math.sqrt(dxg*dxg + dyg*dyg));

            cell.h = (int)((double)1000*Math.sqrt(dxh*dxh + dyh*dyh));

            cell.f = cell.g+cell.h;

            int openIndex   = isInList(openSet,cell);
            int closedIndex = isInList(closedSet,cell);
            if (openIndex == -1 && closedIndex == -1) {

                openSet.add(cell);

            } else {
                if (openIndex > -1){
                    if (openSet.get(openIndex).f <= cell.f) {
                    } else {
                        openSet.remove(openIndex);
                        openSet.add(cell);
                    }
                } else {
                    if (closedSet.get(closedIndex).f <= cell.f) {
                    } else {
                        closedSet.remove(closedIndex);
                        openSet.add(cell);
                    }
                }
            }
        }

    }


    private ArrayList<Cell> createSuccesors(Cell current){
        int x = current.x;
        int y = current.y;
        ArrayList<Cell> temp = new ArrayList<>();

        int inlist = isInList(node,current);
        if (inlist != -1){

            for (int i=0;i<node.get(inlist).neighbors.size();i++) {
                if (isInList(openSet,node.get(inlist).neighbors.get(i)) == -1 &&
                        isInList(closedSet,node.get(inlist).neighbors.get(i)) == -1) {
                    Cell cell = node.get(inlist).neighbors.get(i);

                    cell.prev = current;

                    temp.add(cell);
                }
            }

        }
        else {

            ArrayList<Cell> nei = new ArrayList<>();
            for (int i=0;i<node.size();i++){
                int dxg = current.x-node.get(i).x;
                int dyg = current.y-node.get(i).y;

                Cell  celly = node.get(i);
                celly.g = current.g+(int)((double)1000*Math.sqrt(dxg*dxg + dyg*dyg));

                celly.prev = current;
                nei.add(celly);
            }

            Collections.sort(nei, new CellComparatorByG());
            temp.add(nei.get(0));
            temp.add(nei.get(1));
            temp.add(nei.get(2));

        }
        return temp;
    }

    private int isInList(ArrayList<Cell> list, Cell current){
        int index = -1;
        for (int i = 0 ; i < list.size(); i++) {
            if (current.x == list.get(i).x && current.y == list.get(i).y) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void checkTermination() {

            expandNode();
            if (found) {
                endOfSearch = true;
                route = plotRoute();
            }
    }

    public ArrayList<Point> plotRoute() {
        searching = false;
        findstart = false;
        endOfSearch = true;
        ArrayList<Point> route = new ArrayList<>();
        int steps = 0;
        double distance = 0;
        int index = isInList(closedSet, targetPos);
        Cell cur = closedSet.get(index);

        while (!findstart) {

            steps++;
            distance++;

            route.add(new Point(cur.x, cur.y));
            cur = cur.prev;
            if ((cur.x == robotStart.x) && (cur.y == robotStart.y)) {
                route.add(new Point(cur.x, cur.y));
                findstart = true;
            }

        }

        return route;
    }

    public void search(){
        while (!endOfSearch){
            checkTermination();
        }
    }

}

