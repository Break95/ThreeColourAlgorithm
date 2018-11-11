import java.util.*;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

class ThreeColourAlgorithm {

    Graph graph = new SingleGraph("Exercise 6");
    Integer[] flow = {0,0,0,0,0,0,0,0};
    //Integer[] flow = {0,0,2,0,0};
    List<Edge> incompatible_edges = new ArrayList<Edge>();
    Edge selected_edge;
    boolean compatibility;

    ThreeColourAlgorithm(){
        //Default graph
        graph.addNode("1");
        graph.addNode("2");
        graph.addNode("3");
        graph.addNode("4");
        graph.addNode("5");
        graph.addNode("6");

        graph.getNode("1").setAttribute("xy", 1, 2);
        graph.getNode("2").setAttribute("xy", 2, 3);
        graph.getNode("3").setAttribute("xy", 2, 1);
        graph.getNode("4").setAttribute("xy", 3, 3);
        graph.getNode("5").setAttribute("xy", 3, 1);
        graph.getNode("6").setAttribute("xy", 4, 2);

        graph.getNode("1").setAttribute("ui.label", "1");
        graph.getNode("2").setAttribute("ui.label", "2");
        graph.getNode("3").setAttribute("ui.label", "3");
        graph.getNode("4").setAttribute("ui.label", "4");
        graph.getNode("5").setAttribute("ui.label", "5");
        graph.getNode("6").setAttribute("ui.label", "6");

        graph.getNode("1").setAttribute("ui.style", "text-alignment: above;");
        graph.getNode("2").setAttribute("ui.style", "text-alignment: above;");
        graph.getNode("3").setAttribute("ui.style", "text-alignment: above;");
        graph.getNode("4").setAttribute("ui.style", "text-alignment: above;");
        graph.getNode("5").setAttribute("ui.style", "text-alignment: above;");
        graph.getNode("6").setAttribute("ui.style", "text-alignment: above;");

        graph.addEdge("a1","1","2", true);
        graph.getEdge("a1").setAttribute("min_flow", "2");
        graph.getEdge("a1").setAttribute("max_flow", "4");
        graph.getEdge("a1").setAttribute("unit_cost", "3");
        graph.getEdge("a1").setAttribute("ui.label", "a1");
        graph.getEdge("a1").addAttribute("ui.style", "text-background-mode: plain;");

        graph.addEdge("a2","1","3", true);
        graph.getEdge("a2").setAttribute("min_flow", "3");
        graph.getEdge("a2").setAttribute("max_flow", "7");
        graph.getEdge("a2").setAttribute("unit_cost", "2");
        graph.getEdge("a2").setAttribute("ui.label", "a2");
        graph.getEdge("a2").addAttribute("ui.style", "text-background-mode: plain;");

        graph.addEdge("a3","2","4", true);
        graph.getEdge("a3").setAttribute("min_flow", "1");
        graph.getEdge("a3").setAttribute("max_flow", "3");
        graph.getEdge("a3").setAttribute("unit_cost", "2");
        graph.getEdge("a3").setAttribute("ui.label", "a3");
        graph.getEdge("a3").addAttribute("ui.style", "text-background-mode: plain;");

        graph.addEdge("a4","2","5", true);
        graph.getEdge("a4").setAttribute("min_flow", "0");
        graph.getEdge("a4").setAttribute("max_flow", "2");
        graph.getEdge("a4").setAttribute("unit_cost", "1");
        graph.getEdge("a4").setAttribute("ui.label", "a4");
        graph.getEdge("a4").addAttribute("ui.style", "text-alignment: left; text-background-mode: plain;");

        graph.addEdge("a5","3","4", true);
        graph.getEdge("a5").setAttribute("min_flow", "2");
        graph.getEdge("a5").setAttribute("max_flow", "5");
        graph.getEdge("a5").setAttribute("unit_cost", "3");
        graph.getEdge("a5").setAttribute("ui.label", "a5");
        graph.getEdge("a5").addAttribute("ui.style", "text-background-mode: plain;");

        graph.addEdge("a6","3","5", true);
        graph.getEdge("a6").setAttribute("min_flow", "2");
        graph.getEdge("a6").setAttribute("max_flow", "4");
        graph.getEdge("a6").setAttribute("unit_cost", "1");
        graph.getEdge("a6").setAttribute("ui.label", "a6");
        graph.getEdge("a6").addAttribute("ui.style", "text-background-mode: plain;");

        graph.addEdge("a7","4","6", true);
        graph.getEdge("a7").setAttribute("min_flow", "0");
        graph.getEdge("a7").setAttribute("max_flow", "3");
        graph.getEdge("a7").setAttribute("unit_cost", "3");
        graph.getEdge("a7").setAttribute("ui.label", "a7");
        graph.getEdge("a7").addAttribute("ui.style", "text-background-mode: plain;");

        graph.addEdge("a8","5","6", true);
        graph.getEdge("a8").setAttribute("min_flow", "1");
        graph.getEdge("a8").setAttribute("max_flow", "5");
        graph.getEdge("a8").setAttribute("unit_cost", "4");
        graph.getEdge("a8").setAttribute("ui.label", "a8");
        graph.getEdge("a8").addAttribute("ui.style", "text-background-mode: plain;");

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        for (Edge ed : graph.getEachEdge()){
            ed.addAttribute("visited","false");
        }
    }

    void check_compatibility(){
        compatibility = true;

        incompatible_edges = new ArrayList<Edge>();

        for ( int i = 0; i < this.flow.length; i++){
            int max = Integer.parseInt((String) graph.getEdge(i).getAttribute("max_flow"));
            int min = Integer.parseInt((String) graph.getEdge(i).getAttribute("min_flow"));

            if (!(min <= flow[i] ) || !(flow[i] <= max )) {
                System.out.println("Not compatible in " + graph.getEdge(i).toString());
                incompatible_edges.add(graph.getEdge(i));
                compatibility = false;
            }
        }
    }

    void choose_arc(){
        if(incompatible_edges.isEmpty())
            System.out.println("No incompatible edges");

        selected_edge = incompatible_edges.get(0);

        if(flow[selected_edge.getIndex()] > Integer.parseInt((String) selected_edge.getAttribute("max_flow")))
            colour_edges_a();
        else
            colour_edges_b();
    }

    private void colour_edges_a(){
        for (Edge ed : graph.getEachEdge()) {
            int ed_min_flow = Integer.parseInt((String) ed.getAttribute("min_flow"));
            int ed_max_flow = Integer.parseInt((String) ed.getAttribute("max_flow"));

            //Reset edge attibutes
            graph.getEdge(ed.getId()).removeAttribute("ui.style");
            graph.getEdge(ed.getId()).removeAttribute("set");

            int act_edge_flow = flow[ed.getIndex()];

            // Black
            if (ed.getId().equals(selected_edge.getId()) || act_edge_flow >= ed_max_flow){
                graph.getEdge(ed.getId()).addAttribute("ui.style", "fill-color: black;");
                graph.getEdge(ed.getId()).addAttribute("set", "black");
                continue;
            }

            //Red
            if(act_edge_flow > ed_min_flow) {
                graph.getEdge(ed.getId()).addAttribute("ui.style", " fill-color: red;");
                graph.getEdge(ed.getId()).addAttribute("set", "red");
                continue;
            }

            //Green
            if(act_edge_flow <= ed_min_flow) {
                graph.getEdge(ed.getId()).addAttribute("ui.style", "fill-color: green;");
                graph.getEdge(ed.getId()).addAttribute("set", "green");
            }
        }
    }

    private void colour_edges_b(){
        for (Edge ed : graph.getEachEdge()) {
            int ed_min_flow = Integer.parseInt((String) ed.getAttribute("min_flow"));
            int ed_max_flow = Integer.parseInt((String) ed.getAttribute("max_flow"));

            //Reset edge attributes
            graph.getEdge(ed.getId()).removeAttribute("ui.style");
            graph.getEdge(ed.getId()).removeAttribute("set");

            int act_edge_flow = flow[ed.getIndex()];

            // Black
            if (ed.getId().equals(selected_edge.getId()) || act_edge_flow <= ed_min_flow){
                graph.getEdge(ed.getId()).addAttribute("ui.style", "fill-color: black;");
                graph.getEdge(ed.getId()).addAttribute("set", "black");
                continue;
            }

            //Red
            if(act_edge_flow < ed_max_flow) {
                graph.getEdge(ed.getId()).addAttribute("ui.style", " fill-color: red;");
                graph.getEdge(ed.getId()).addAttribute("set", "red");
                continue;
            }

            //Green
            if(act_edge_flow >= ed_max_flow) {
                graph.getEdge(ed.getId()).addAttribute("ui.style", "fill-color: green;");
                graph.getEdge(ed.getId()).addAttribute("set", "green");
            }
        }
    }

    List<Edge> cycle = new ArrayList<Edge>();

    boolean find_cycle(Edge edge){
        //Check if cycle
        if (edge.getNode1().equals(this.selected_edge.getNode0()))
            return true;

        //Actual edge visited.
        graph.getEdge(edge.getId()).setAttribute("visited", "true");

        //For all edges reachable from destination node of actual edge.
        for (Edge ed: edge.getNode1().getEachLeavingEdge()){
            if (ed.getAttribute("visited").equals("false"))
                if (find_cycle(ed)) {
                    cycle.add(ed);
                    return true;
                }
        }

        graph.getEdge(selected_edge.getId()).setAttribute("visited", "false");
        return false;
    }

    void clear_visited(){
        for(Edge ed: graph.getEachEdge()){
            graph.getEdge(ed.getId()).setAttribute("visited", "false");
        }

        for (Edge ed : cycle)
            if (ed.getId().equals("a0")) {
                cycle.remove(ed);
                break;
            }
    }

    void update_flow(){
        if(flow[selected_edge.getIndex()] > Integer.parseInt((String) selected_edge.getAttribute("max_flow")))
            update_flow_a();
        else
            update_flow_b();
    }

    private void update_flow_a(){
        int[] values = new int[cycle.size()];
        Arrays.fill(values, 0);
        int i = 0;

        //Get minimum values
        for (Edge ed: cycle){
            int ed_min_flow = Integer.parseInt((String) ed.getAttribute("min_flow"));
            int ed_max_flow = Integer.parseInt((String) ed.getAttribute("max_flow"));
            int actual_flow = flow[ed.getIndex()];

            if(ed.getAttribute("set").equals("black"))
                values[i] = actual_flow - ed_min_flow;

            if(ed.getAttribute("set").equals("green"))
                values[i] = ed_max_flow - actual_flow;

            if(ed.getAttribute("set").equals("red"))
                values[i] = ed_max_flow - actual_flow;

            i++;
        }

        Integer act_min = Integer.MAX_VALUE;

        //Get minimum
        for(Integer min : values) {
            if (min < act_min)
                act_min = min;
        }

        //Flow Update
        for (Edge ed : cycle){
            if(ed.getAttribute("set").equals("black")) {
                this.flow[ed.getIndex()] -= act_min;
                continue;
            }
            else
                this.flow[ed.getIndex()] += act_min;
        }
        cycle = new ArrayList<Edge>();
    }

    private void update_flow_b(){
        int[] values = new int[cycle.size()];
        Arrays.fill(values, 0);
        int i = 0;


        //Set minimums
        for (Edge ed: cycle){
            int ed_min_flow = Integer.parseInt((String) ed.getAttribute("min_flow"));
            int ed_max_flow = Integer.parseInt((String) ed.getAttribute("max_flow"));
            int actual_flow = flow[ed.getIndex()];

            if(ed.getAttribute("set").equals("black"))
                values[i] = ed_max_flow - actual_flow;

            if(ed.getAttribute("set").equals("green"))
                values[i] = actual_flow - ed_min_flow;

            if(ed.getAttribute("set").equals("red"))
                values[i] = actual_flow - ed_min_flow;

            i++;
        }

        Integer act_min = Integer.MAX_VALUE;

        //Get minimum
        for(Integer min : values) {
            if (min < act_min)
                act_min = min;
        }

        //Flow Update
        for (Edge ed : cycle){
            if(ed.getAttribute("set").equals("black")) {
                this.flow[ed.getIndex()] += act_min;
                continue;
            }
            else
                this.flow[ed.getIndex()] -= act_min;
        }

        cycle = new ArrayList<Edge>();
    }
}
