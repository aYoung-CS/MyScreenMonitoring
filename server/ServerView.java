package server;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.sun.glass.ui.Menu;
import src.server.Server;
/**
 * ��װ�������˵���ͼ��
 * @author Administrator
 *
 */
public class ServerView {
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    private int width;
    private int height;

    //���±���Ϊ��̬�ģ�������HandleClient����ʲ���
    static DefaultTreeModel model;
    static DefaultMutableTreeNode root;
    static src.server.DrawPanel centerPanel;
    static List<String> list=new ArrayList<>();
    public void View(){
        width = (int) (screensize.getWidth()*0.7);
        height = (int) (screensize.getHeight()*0.8);
    }

    //������ͼ
    public void create(){
        //�õ����ݴ���
        JFrame frame=new JFrame("Զ����Ļ����ϵͳ");
        Container container=frame.getContentPane();

        //���
        JPanel leftPanel=new JPanel();
        leftPanel.setBackground(Color.darkGray);
        container.add(leftPanel,BorderLayout.WEST);
        //��
        root=new DefaultMutableTreeNode("�������ӵı��ض�");
        model=new DefaultTreeModel(root);
        JTree tree=new JTree(model);
        tree.setBackground(Color.darkGray);

        tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                JTree tree=(JTree) e.getSource();
                DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) tree
                        .getLastSelectedPathComponent();
                String nodeName=selectionNode.toString();
                Server.curKey=nodeName;
            }
        });


        //�������ڵ����ʽ
        DefaultTreeCellRenderer cr=new DefaultTreeCellRenderer();
        cr.setBackgroundNonSelectionColor(Color.darkGray);
        cr.setTextNonSelectionColor(Color.white);
        tree.setCellRenderer(cr);
        JScrollPane jsp=new JScrollPane(tree);
        JScrollBar bar=jsp.getHorizontalScrollBar();
        bar.setBackground(Color.darkGray);
        jsp.setBorder(null);
        leftPanel.add(jsp);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                Server.serverLive=false;
            }

        });
        centerPanel=new src.server.DrawPanel();
        container.add(new JScrollPane(centerPanel));
        frame.setSize(width,height);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * ������ڵ�
     * @param l
     */
    public static void setTreeNode(List<String> l){
        list=l;
        root.removeAllChildren();
        for(int i=0;i<list.size();i++){
            DefaultMutableTreeNode node1=new DefaultMutableTreeNode(list.get(i));
            root.add(node1);
        }
        model.reload();
    }

    public List<String> addValue(String key){
        list.add(key);
        return list;
    }

    public List<String> removeValue(String key){
        list.remove(key);
        return list;
    }

    public void clear(){
        list.clear();
    }

}
