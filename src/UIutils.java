import javax.swing.*;
import java.awt.*;

public class UIutils {
    public static JButton createStyledButton(String text, Color bg, String iconPath) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        if (iconPath != null) {
            try {
                ImageIcon icon = new ImageIcon(UIutils.class.getResource(iconPath));
                // 🔹 Scale icon to 20x20
                Image scaled = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                System.out.println("⚠ Icon not found: " + iconPath);
            }
        }

        return btn;
    }
}
