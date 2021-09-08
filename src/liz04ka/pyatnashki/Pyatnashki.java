package liz04ka.pyatnashki;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Pyatnashki extends JFrame {
    private JPanel panel = new JPanel(new GridLayout(4, 4, 2, 2));
    private int[][] numbers = new int[4][4];

    public Pyatnashki() {
        super("Pyatnashki");

        setBounds(200, 200, 300, 300);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        createMenu();

        Container container = getContentPane();
        panel.setDoubleBuffered(true);
        container.add(panel);

        generate();
        repaintField();

        addKeyListener(new ArrowKeyListener());
    }

    private final Random generator = new Random();
    private final int[] invariants = new int[16];

    public void generate() {
        for (int i = 0; i < 16; i++) {
            numbers[i / 4][i % 4] = 0;
            invariants[i] = 0;
        }
        numbers[3][3] = -1;

        for (int i = 1; i < 16; i++) {
            int k, l;
            do {
                k = generator.nextInt(4);
                l = generator.nextInt(4);
            } while (numbers[k][l] != 0);
            numbers[k][l] = i;
            invariants[k * 4 + l] = i;
        }

        if (!canBeSolved(invariants))
            generate();
    }

    private boolean canBeSolved(int[] invariants) {
        int sum = 0;
        for (int i = 0; i < 16; i++) {
            if (invariants[i] == 0) {
                sum += i / 4;
                continue;
            }

            for (int j = i + 1; j < 16; j++) {
                if (invariants[j] < invariants[i])
                    sum++;
            }
        }
        System.out.println(sum % 2 == 0);
        return sum % 2 == 0;
    }

    public void repaintField() {
        panel.removeAll();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                JButton button = new JButton(Integer.toString(numbers[i][j]));
                button.setFocusable(false);
                panel.add(button);
                if (numbers[i][j] == -1)
                    button.setVisible(false);
                else
                    button.addActionListener(new ClickListener());
            }
        }

        panel.validate();
        panel.repaint();
    }

    public boolean checkWin() {
        for (int i = 0; i < 15; i++)
            if (numbers[i / 4][i % 4] != i + 1)
                return false;
        return true;
    }

    private void createMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        for (String fileItem : new String[]{"New", "Exit"}) {
            JMenuItem item = new JMenuItem(fileItem);
            item.setMnemonic(fileItem.charAt(0));
            item.setAccelerator(KeyStroke.getKeyStroke(fileItem.charAt(0), InputEvent.CTRL_DOWN_MASK));
            item.setActionCommand(fileItem.toLowerCase());
            item.addActionListener(new NewMenuListener());
            fileMenu.add(item);
        }
        fileMenu.insertSeparator(1);

        menu.add(fileMenu);

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK));
        aboutItem.setActionCommand("about");
        aboutItem.addActionListener(new NewMenuListener());

        menu.add(aboutItem);

        setJMenuBar(menu);
    }

    public int getCellIndex(int num) {
        for (int i = 0; i < 16; i++)
            if (numbers[i / 4][i % 4] == num)
                return i;

        return -1;
    }

    public void change(int num) {
        int cellIndex = getCellIndex(num);
        int i = cellIndex / 4, j = cellIndex % 4;

        if (i > 0 && numbers[i - 1][j] == -1) {
            numbers[i - 1][j] = num;
            numbers[i][j] = -1;
        } else if (i < 3 && numbers[i + 1][j] == -1) {
            numbers[i + 1][j] = num;
            numbers[i][j] = -1;
        } else if (j > 0 && numbers[i][j - 1] == -1) {
            numbers[i][j - 1] = num;
            numbers[i][j] = -1;
        } else if (j < 3 && numbers[i][j + 1] == -1) {
            numbers[i][j + 1] = num;
            numbers[i][j] = -1;
        }

        repaintField();

        if (checkWin()) {
            JOptionPane.showMessageDialog(null, "YOU ARE BEST! YOU WIN!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
            generate();
            repaintField();
        }
    }

    class ArrowKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            int cellIndex = getCellIndex(-1);

            int ind = -1;

            if (key == KeyEvent.VK_UP)
                ind = cellIndex + 4;
            else if (key == KeyEvent.VK_DOWN)
                ind = cellIndex - 4;
            else if (key == KeyEvent.VK_RIGHT)
                ind = cellIndex - 1;
            else if (key == KeyEvent.VK_LEFT)
                ind = cellIndex + 1;

            if (ind >= 0 && ind < 16) change(numbers[ind / 4][ind % 4]);
        }
    }

    private class NewMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            System.out.println(command);
            if ("exit".equals(command)) {
                System.exit(0);
            } else if ("new".equals(command)) {
                generate();
                repaintField();
            } else if ("about".equals(command)) {
                JOptionPane.showMessageDialog(panel, "Liza Efremova P3168\n(C) 2021");
            }
        }
    }

    private class ClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            button.setVisible(false);
            String name = button.getText();
            change(Integer.parseInt(name));
        }
    }

    public static void main(String[] args) {
        JFrame app = new Pyatnashki();
        app.setVisible(true);
    }
}
