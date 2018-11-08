import mdlaf.MaterialLookAndFeel;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.graph.*;
import scala.util.parsing.combinator.testing.Str;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    static ThreeColourAlgorithm algo = new ThreeColourAlgorithm();
    static String flow_format = "%5d  %5d  %5d  %5d  %5d  %5d  %5d  %5d";
    //static String flow_format = "%5d  %5d  %5d  %5d  %5d ";
    static int iter = 0;
    static JTextArea data = new JTextArea();
    static JTextArea info = new JTextArea();

    static JTable vals;

    static int state = 0;

    public static void main(String args[]){

        try{
            //UIManager.setLookAndFeel(new MaterialLookAndFeel());
            System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createUI();
            }
        });
    }

    public static void addComponentsToPane (Container pane) {
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        //c.fill = GridBagConstraints.NONE;


        //Graph JPanel
        JPanel panel = new JPanel(new GridLayout());
        panel.setSize(640,480);
        panel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        Viewer viewer = new Viewer(algo.graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        ViewPanel viewPanel = viewer.addDefaultView(false);

        c.ipadx = 640;
        c.ipady = 480;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(viewPanel, c);

        //Data JPanel - FLOW
        String data1 = "Flow\n";
        String data2 = String.format("%27s %5s  %5s  %5s  %5s  %5s  %5s  %5s\n", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8");
        //String data2 = String.format("%27s %5s  %5s  %5s  %5s\n", "f1", "f2", "f3", "f4", "f5");
        String data3 = "Iteration " + iter + ": ";
        data3 += String.format(flow_format, algo.flow);

        data.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        data.setEditable(false);
        //data.setSize(150,480);
        data.insert(data1 + data2 + data3, 0);

        final JScrollPane scroll_data = new JScrollPane(data);
        scroll_data.setSize(320,240);

        c.gridwidth = 1;
        c.ipadx = 0;
        //c.fill = GridBagConstraints.VERTICAL;

        c.ipady = 120;
        c.gridx = 0;
        c.gridy = 1;
       // pane.add(scroll_data, c);

        //Info Panel
        String info1 = "Information";
        info.append(info1);
        info.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        info.setEditable(false);

        final JScrollPane scroll_info = new JScrollPane(info);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx = 400;
        c.ipady = 240;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(scroll_info, c);

        //frame.add(scroll_info);

        c.fill = GridBagConstraints.NONE;

        //Step Button
        JPanel buttons = new JPanel(new GridLayout());

        final JButton step = new JButton("Step");
        final JButton iteration = new JButton("Iteration");

        iteration.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch (state){
                    case 0:
                        algo.check_compatibility();

                        if (algo.compatibility == true){
                            data.append("\n\n The flow is now compatible.");
                            info.append("\n\n   The flow is now compatible.");
                            step.setEnabled(false);
                            iteration.setEnabled(false);
                            break;
                        }

                        info.append("\n   Iteration " + (iter + 1) + ": ");
                        String temp = "";
                        for (Edge ed : algo.incompatible_edges){ temp += ed.toString() + ",  "; }
                        temp = temp.substring(0, temp.length()-3);
                        info.append("\n      Incompatible edges: " + temp);
                        temp = "";
                        state += 1;

                    case 1:
                        algo.choose_arc();
                        info.append("\n      Chosen arc: " + algo.selected_edge.toString());
                        state += 1;

                    case 2:
                        //algo.graph.addEdge("a0", "t", "s", true);
                        algo.graph.addEdge("a0","6","1",true);
                        algo.graph.getEdge("a0").addAttribute("visited", "false");
                        algo.find_cycle(algo.selected_edge);
                        algo.cycle.add(algo.selected_edge);
                        algo.graph.removeEdge("a0");

                        temp = "";
                        for (Edge ed : algo.cycle){ temp += ed.toString() + ",  "; }
                        temp = temp.substring(0, temp.length()-3);
                        info.append("\n      Cycle: " + temp);
                        temp = "";

                        algo.clear_visited();
                        state += 1;

                    case 3:
                        algo.update_flow();
                        vals.setValueAt(algo.flow[0].toString(),1,1);
                        vals.setValueAt(algo.flow[1].toString(),1,2);
                        vals.setValueAt(algo.flow[2].toString(),1,3);
                        vals.setValueAt(algo.flow[3].toString(),1,4);
                        vals.setValueAt(algo.flow[4].toString(),1,5);
                        vals.setValueAt(algo.flow[5].toString(),1,6);
                        vals.setValueAt(algo.flow[6].toString(),1,7);
                        vals.setValueAt(algo.flow[7].toString(),1,8);
                        iter += 1;
                        state = 0;
                        String out = "\nIteration " + iter + ": " + String.format(flow_format, algo.flow);
                        data.append(out);
                }
            }
        });

        step.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch (state){
                    //Check Compatibility and choose arc. (Indicate if f > ca or f < ba
                    case 0:
                        algo.check_compatibility();

                        if (algo.compatibility == true){
                            data.append("\n\n The flow is now compatible.");
                            info.append("\n\n   The flow is now compatible.");
                            step.setEnabled(false);
                            iteration.setEnabled(false);
                            break;
                        }

                        info.append("\n   Iteration " + (iter + 1) + ": ");
                        String temp = "";
                        for (Edge ed : algo.incompatible_edges){ temp += ed.toString() + ",  "; }
                        temp = temp.substring(0, temp.length()-3);
                        info.append("\n      Incompatible edges: " + temp);
                        temp = "";
                        state += 1;
                        break;

                    //Choose arc and colour Graph
                    case 1:
                        algo.choose_arc();
                        info.append("\n      Chosen arc: " + algo.selected_edge.toString());
                        state += 1;
                        break;

                    //Get cycle
                    case 2:
                        //algo.graph.addEdge("a0", "t", "s", true);
                        algo.graph.addEdge("a0","6","1",true);
                        algo.graph.getEdge("a0").addAttribute("visited", "false");
                        algo.find_cycle(algo.selected_edge);
                        algo.cycle.add(algo.selected_edge);
                        algo.graph.removeEdge("a0");

                        temp = "";
                        for (Edge ed : algo.cycle){ temp += ed.toString() + ",  "; }
                        temp = temp.substring(0, temp.length()-3);
                        info.append("\n      Cycle: " + temp);
                        temp = "";

                        algo.clear_visited();
                        state += 1;
                        break;

                    //Update flow
                    case 3:
                        algo.update_flow();
                        vals.setValueAt(algo.flow[0].toString(),1,1);
                        vals.setValueAt(algo.flow[1].toString(),1,2);
                        vals.setValueAt(algo.flow[2].toString(),1,3);
                        vals.setValueAt(algo.flow[3].toString(),1,4);
                        vals.setValueAt(algo.flow[4].toString(),1,5);
                        vals.setValueAt(algo.flow[5].toString(),1,6);
                        vals.setValueAt(algo.flow[6].toString(),1,7);
                        vals.setValueAt(algo.flow[7].toString(),1,8);
                        iter += 1;
                        state = 0;
                        String out = "\nIteration " + iter + ": " + String.format(flow_format, algo.flow);
                        data.append(out);
                        break;
                }
            }
        });

        buttons.add(step);
        buttons.add(iteration);

        c.ipadx = 0;
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 3;
        pane.add(buttons, c);

        //frame.add(buttons);



        //Table panel
        String[] columnNames = {"","a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8"};

        String datas[][] = {{"(b,c)", "(2,4)", "(3,7)", "(1,3)",
                "(0,2)", "(2,5)", "(2,4)", "(0,3)", "(1,5)"},
                {"f",
                        algo.flow[0].toString(),
                        algo.flow[1].toString(),
                        algo.flow[2].toString(),
                        algo.flow[3].toString(),
                        algo.flow[4].toString(),
                        algo.flow[5].toString(),
                        algo.flow[6].toString(),
                        algo.flow[7].toString()}
        };

        vals = new JTable(datas, columnNames);

        //vals.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth =2;

        c.gridx = 0;
        c.gridy = 2;

        c.ipady = 33;
        JScrollPane xd = new JScrollPane(vals);
        pane.add(xd, c);

        //Packing
        //frame.pack();
       // frame.setLocationRelativeTo(null);
       // frame.setVisible(true);
    }

    private static void createUI(){
        //Create and set up the window.
        JFrame frame = new JFrame("Three Colour Algorithm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }


}
