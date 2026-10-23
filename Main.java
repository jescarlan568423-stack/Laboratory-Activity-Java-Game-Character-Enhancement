package CCE105;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Main extends JFrame {
    private static final int FRAMES = 36;                
    private static final int MAX_FIGHTERS = 8;
    private static final Color BG = new Color(24, 24, 48);
    private static final Color GOLD = new Color(255, 205, 80);

    private final Queue<GameCharacter> fighters = new LinkedList<>();

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    private final DefaultListModel<String> rosterModel = new DefaultListModel<>();
    private final JLabel setupStatus = new JLabel(" ");
    private JTextField nameField;
    private JComboBox<String> typeBox;

    private JPanel arena;
    private JTextArea logArea;
    private JButton attackBtn, specialBtn;
    private JLabel turnLabel;
    private GameCharacter left, right;
    private boolean leftTurn, animating, matchOver, leftIsAttacker, specialAnim;
    private double shownLeftHp, shownRightHp;
    private int frame, lastDamage;
    private final Timer timer;

    public Main() {
        super("Battle Arena");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        root.add(buildSetupPanel(), "setup");
        root.add(buildBattlePanel(), "battle");
        add(root);
        timer = new Timer(16, e -> tick());
        setSize(820, 600);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    private JPanel buildSetupPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel title = new JLabel("BATTLE ARENA", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(GOLD);
        JLabel sub = new JLabel("Create your fighters, then battle in a knockout tournament!",
                SwingConstants.CENTER);
        sub.setForeground(new Color(190, 190, 230));
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(title);
        top.add(sub);
        panel.add(top, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        JLabel nameLabel = makeLabel("Fighter name:");
        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.addActionListener(e -> addCharacter());

        JLabel typeLabel = makeLabel("Class:");
        typeBox = new JComboBox<>(new String[]{"Warrior", "Mage"});
        typeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        typeBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel info = makeLabel("");
        info.setForeground(new Color(170, 200, 255));
        info.setText(describe("Warrior"));
        typeBox.addActionListener(e -> info.setText(describe((String) typeBox.getSelectedItem())));

        JButton addBtn = makeButton("Add Fighter", new Color(60, 140, 90));
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.addActionListener(e -> addCharacter());

        setupStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        setupStatus.setFont(new Font("SansSerif", Font.BOLD, 13));

        form.add(nameLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(nameField);
        form.add(Box.createVerticalStrut(14));
        form.add(typeLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(typeBox);
        form.add(Box.createVerticalStrut(10));
        form.add(info);
        form.add(Box.createVerticalStrut(18));
        form.add(addBtn);
        form.add(Box.createVerticalStrut(12));
        form.add(setupStatus);

        JPanel rosterPanel = new JPanel(new BorderLayout(0, 6));
        rosterPanel.setOpaque(false);
        JLabel rosterTitle = makeLabel("Fighters in queue (max " + MAX_FIGHTERS + "):");
        JList<String> rosterList = new JList<>(rosterModel);
        rosterList.setFont(new Font("SansSerif", Font.PLAIN, 15));
        rosterList.setBackground(new Color(40, 40, 80));
        rosterList.setForeground(Color.WHITE);
        rosterPanel.add(rosterTitle, BorderLayout.NORTH);
        rosterPanel.add(new JScrollPane(rosterList), BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(1, 2, 30, 0));
        center.setOpaque(false);
        center.add(form);
        center.add(rosterPanel);
        panel.add(center, BorderLayout.CENTER);

        JButton startBtn = makeButton("START BATTLE", new Color(200, 120, 30));
        startBtn.addActionListener(e -> startTournament());
        JButton clearBtn = makeButton("Clear Fighters", new Color(120, 60, 60));
        clearBtn.addActionListener(e -> {
            fighters.clear();
            refreshRoster();
            showStatus("Fighter list cleared.", false);
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottom.setOpaque(false);
        bottom.add(startBtn);
        bottom.add(clearBtn);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private String describe(String type) {
        return type.equals("Warrior")
                ? "<html>Tough fighter: 120 HP.<br>Shield Bash also heals 8 HP.</html>"
                : "<html>Glass cannon: 80 HP.<br>Lightning Storm hits very hard.</html>";
    }

    private void addCharacter() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showStatus("Please enter a name.", true);
            return;
        }
        if (!name.matches("[A-Za-z0-9 ]{1,12}")) {
            showStatus("Name: 1-12 letters, numbers or spaces only.", true);
            return;
        }
        if (fighters.size() >= MAX_FIGHTERS) {
            showStatus("The fighter list is full (max " + MAX_FIGHTERS + ").", true);
            return;
        }
        for (GameCharacter c : fighters) {
            if (c.getName().equalsIgnoreCase(name)) {
                showStatus("That name is already taken.", true);
                return;
            }
        }
        GameCharacter fighter = typeBox.getSelectedItem().equals("Warrior")
                ? new Warrior(name) : new Mage(name);
        fighters.offer(fighter);                 // enqueue
        refreshRoster();
        nameField.setText("");
        nameField.requestFocus();
        showStatus(name + " joined the arena!", false);
    }

    private void refreshRoster() {
        rosterModel.clear();
        for (GameCharacter c : fighters) {
            rosterModel.addElement(c.showName() + "  (" + c.getType() + ")");
        }
    }

    private void showStatus(String text, boolean error) {
        setupStatus.setForeground(error ? new Color(255, 110, 110) : new Color(120, 230, 150));
        setupStatus.setText(text);
    }

    private void startTournament() {
        if (fighters.size() < 2) {
            showStatus("Add at least 2 fighters to start!", true);
            return;
        }
        GameCharacter a = fighters.poll();       
        GameCharacter b = fighters.poll();
        startMatch(a, b);
    }

    private JPanel buildBattlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        turnLabel = new JLabel(" ", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Serif", Font.BOLD, 26));
        turnLabel.setForeground(GOLD);
        turnLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        panel.add(turnLabel, BorderLayout.NORTH);

        arena = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawArena((Graphics2D) g, getWidth(), getHeight());
            }
        };
        arena.setPreferredSize(new Dimension(800, 340));
        panel.add(arena, BorderLayout.CENTER);

        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        logArea.setBackground(new Color(15, 15, 35));
        logArea.setForeground(new Color(220, 220, 255));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane logScroll = new JScrollPane(logArea);

        attackBtn = makeButton("Attack", new Color(180, 60, 60));
        attackBtn.addActionListener(e -> performAction(false));
        specialBtn = makeButton("Special Move (50 energy)", new Color(90, 80, 190));
        specialBtn.addActionListener(e -> performAction(true));
        JButton quitBtn = makeButton("Back to Menu", new Color(90, 90, 110));
        quitBtn.addActionListener(e -> backToMenu());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttons.setBackground(BG);
        buttons.add(attackBtn);
        buttons.add(specialBtn);
        buttons.add(quitBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.add(logScroll, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void startMatch(GameCharacter a, GameCharacter b) {
        left = a;
        right = b;
        left.resetForBattle();
        right.resetForBattle();
        shownLeftHp = left.getHp();
        shownRightHp = right.getHp();
        leftTurn = true;
        animating = false;
        matchOver = false;
        frame = 0;
        logArea.setText("");
        log(left.showName() + " (" + left.getType() + ")  VS  "
                + right.showName() + " (" + right.getType() + ")");
        updateButtons();
        cards.show(root, "battle");
        timer.start();
    }

    private void performAction(boolean special) {
        if (animating || matchOver) return;
        GameCharacter attacker = leftTurn ? left : right;
        GameCharacter defender = leftTurn ? right : left;

        if (special && !attacker.canUseSpecial()) {
            log("Not enough energy for a special move!");
            return;
        }
        lastDamage = special ? attacker.specialMove(defender) : attacker.attack(defender);
        log((special ? attacker.getSpecialMessage() : attacker.getAttackMessage())
                + "  (" + lastDamage + " damage)");

        leftIsAttacker = leftTurn;
        specialAnim = special;
        frame = 0;
        animating = true;
        updateButtons();
    }

    private void finishTurn() {
        GameCharacter attacker = leftTurn ? left : right;
        GameCharacter defender = leftTurn ? right : left;
        if (!defender.isAlive()) {
            handleWin(attacker, defender);
            return;
        }
        leftTurn = !leftTurn;
        updateButtons();
    }

    private void handleWin(GameCharacter winner, GameCharacter loser) {
        matchOver = true;
        timer.stop();
        updateButtons();
        log(loser.getName() + " has been defeated!");
        arena.repaint();
        JOptionPane.showMessageDialog(this, winner.getName() + " wins the fight!",
                "Victory", JOptionPane.INFORMATION_MESSAGE);

        winner.resetForBattle();
        if (fighters.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    winner.getName() + " is the CHAMPION!", "Tournament Over",
                    JOptionPane.INFORMATION_MESSAGE);
            fighters.offer(winner);
            refreshRoster();
            showStatus("Add more fighters for another tournament.", false);
            cards.show(root, "setup");
        } else {
            GameCharacter next = fighters.poll();
            JOptionPane.showMessageDialog(this,
                    "Next challenger: " + next.getName() + " the " + next.getType(),
                    "Next Fight", JOptionPane.INFORMATION_MESSAGE);
            startMatch(winner, next);
        }
    }

    private void backToMenu() {
        timer.stop();
        animating = false;
        left.resetForBattle();
        right.resetForBattle();
        fighters.offer(left);
        fighters.offer(right);
        refreshRoster();
        cards.show(root, "setup");
    }

    private void updateButtons() {
        GameCharacter current = leftTurn ? left : right;
        boolean ready = !animating && !matchOver;
        attackBtn.setEnabled(ready);
        specialBtn.setEnabled(ready && current.canUseSpecial());
        turnLabel.setText(matchOver ? "Fight over!" : current.getName() + "'s turn");
    }

    private void log(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void tick() {
        if (left == null || right == null) return;
        shownLeftHp = ease(shownLeftHp, left.getHp());
        shownRightHp = ease(shownRightHp, right.getHp());
        if (animating) {
            frame++;
            if (frame >= FRAMES) {
                animating = false;
                frame = 0;
                finishTurn();
            }
        }
        arena.repaint();
    }

    private double ease(double current, double target) {
        double diff = target - current;
        return Math.abs(diff) < 0.5 ? target : current + diff * 0.15;
    }

    private void drawArena(Graphics2D g, int w, int h) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0, 0, new Color(30, 20, 70), 0, h, new Color(95, 55, 125)));
        g.fillRect(0, 0, w, h);
        for (int i = 0; i < 40; i++) {                       
            g.setColor(new Color(255, 255, 255, 90 + (i * 7) % 120));
            g.fillOval((i * 97) % w, (i * 53) % (h / 2), 2, 2);
        }
        int groundY = h - 50;
        g.setColor(new Color(40, 30, 60));
        g.fillRect(0, groundY, w, 50);
        g.setColor(new Color(130, 100, 170));
        g.fillRect(0, groundY, w, 4);

        if (left == null || right == null) return;

        double p = animating ? frame / (double) FRAMES : 0;
        int leftBase = (int) (w * 0.22);
        int rightBase = (int) (w * 0.78);
        int lunge = animating ? (int) (Math.sin(Math.PI * p) * (rightBase - leftBase - 110)) : 0;
        boolean hitPhase = animating && p > 0.45 && p < 0.85;
        int shake = hitPhase ? (int) (Math.sin(frame * 1.5) * 6) : 0;

        int lx = leftBase + (leftIsAttacker ? lunge : shake);
        int rx = rightBase - (leftIsAttacker ? shake : lunge);

        boolean flashLeft = hitPhase && !leftIsAttacker && (frame / 3) % 2 == 0;
        boolean flashRight = hitPhase && leftIsAttacker && (frame / 3) % 2 == 0;

        drawFighter(g, left, lx, groundY, 1, flashLeft, shownLeftHp, leftTurn && !animating && !matchOver);
        drawFighter(g, right, rx, groundY, -1, flashRight, shownRightHp, !leftTurn && !animating && !matchOver);

        if (animating) {
            GameCharacter attacker = leftIsAttacker ? left : right;
            int defX = leftIsAttacker ? rightBase : leftBase;
            if (specialAnim && p > 0.4 && p < 0.8) {
                if (attacker.getType().equals("Mage")) {
                    Random r = new Random(frame / 2);
                    g.setStroke(new BasicStroke(4));
                    g.setColor(new Color(255, 255, 160));
                    int px = defX, py = 0;
                    while (py < groundY - 20) {
                        int nx = defX + r.nextInt(50) - 25;
                        int ny = py + 30;
                        g.drawLine(px, py, nx, ny);
                        px = nx;
                        py = ny;
                    }
                } else {
                    int radius = (int) ((p - 0.4) * 350);
                    g.setStroke(new BasicStroke(6));
                    g.setColor(new Color(255, 170, 60, 200));
                    g.drawOval(defX - radius, groundY - 50 - radius, radius * 2, radius * 2);
                }
                g.setStroke(new BasicStroke(1));
            }
            if (p > 0.45) {
                g.setFont(new Font("SansSerif", Font.BOLD, 30));
                g.setColor(new Color(255, 225, 70));
                g.drawString("-" + lastDamage, defX - 22, (int) (groundY - 215 - (p - 0.45) * 70));
            }
        }
    }

    private void drawFighter(Graphics2D g, GameCharacter c, int x, int groundY, int dir,
                             boolean flash, double shownHp, boolean active) {
        Color body = flash ? new Color(255, 100, 100) : c.getColor();

        g.setColor(new Color(0, 0, 0, 70));                  
        g.fillOval(x - 35, groundY - 8, 70, 16);
        g.setColor(body.darker());                           
        g.fillRect(x - 16, groundY - 40, 12, 40);
        g.fillRect(x + 4, groundY - 40, 12, 40);
        g.setColor(body);                                   
        g.fillRoundRect(x - 24, groundY - 100, 48, 66, 14, 14);
        g.setColor(new Color(245, 210, 170));                
        g.fillOval(x - 17, groundY - 134, 34, 34);
        g.setColor(Color.BLACK);                             
        g.fillOval(x + dir * 7 - 3, groundY - 122, 6, 6);

        if (c.getType().equals("Mage")) {
            g.setColor(body.darker());                       
            g.fillPolygon(new int[]{x - 22, x + 22, x},
                    new int[]{groundY - 130, groundY - 130, groundY - 180}, 3);
            g.setStroke(new BasicStroke(4));                 
            g.setColor(new Color(130, 90, 50));
            g.drawLine(x + dir * 34, groundY - 5, x + dir * 34, groundY - 125);
            g.setColor(new Color(110, 230, 255));            
            g.fillOval(x + dir * 34 - 8, groundY - 138, 16, 16);
        } else {
            g.setColor(Color.GRAY);                          
            g.fillArc(x - 18, groundY - 138, 36, 30, 0, 180);
            g.setColor(new Color(160, 160, 175));            
            g.fillOval(x - dir * 30 - 12, groundY - 88, 24, 38);
            g.setStroke(new BasicStroke(6));                 
            g.setColor(new Color(225, 225, 235));
            g.drawLine(x + dir * 32, groundY - 60, x + dir * 40, groundY - 135);
            g.setStroke(new BasicStroke(5));
            g.setColor(new Color(130, 90, 50));
            g.drawLine(x + dir * 24, groundY - 70, x + dir * 42, groundY - 70);
        }
        g.setStroke(new BasicStroke(1));

        // Name, HP bar, energy bar
        int bx = x - 50, by = groundY - 205;
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String label = c.getName() + " (" + c.getType() + ")";
        g.drawString(label, x - fm.stringWidth(label) / 2, by - 6);

        if (active) {                                       
            g.setColor(GOLD);
            g.fillPolygon(new int[]{x - 8, x + 8, x},
                    new int[]{by - 30, by - 30, by - 18}, 3);
        }

        double ratio = Math.max(0, shownHp) / c.getMaxHp();
        g.setColor(new Color(20, 20, 20));
        g.fillRoundRect(bx, by, 100, 14, 8, 8);
        g.setColor(ratio > 0.5 ? new Color(70, 200, 90)
                : ratio > 0.25 ? new Color(240, 170, 40) : new Color(220, 60, 60));
        g.fillRoundRect(bx, by, (int) (100 * ratio), 14, 8, 8);
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.setColor(Color.WHITE);
        g.drawString(c.getHp() + "/" + c.getMaxHp(), bx + 34, by + 11);

        g.setColor(new Color(20, 20, 20));
        g.fillRoundRect(bx, by + 18, 100, 8, 6, 6);
        g.setColor(new Color(80, 160, 255));
        g.fillRoundRect(bx, by + 18, c.getEnergy(), 8, 6, 6);
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton makeButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setUI(new BasicButtonUI());
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
