package pertanian;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TanamanPanel extends JPanel {

    private JTextField txtID, txtNama, txtJenis, txtHarga;
    private JButton btnSimpan, btnHapus, btnReset, btnUpdate;
    private JTable tableTanaman;
    private DefaultTableModel model;
    private Connection conn;
    private PreparedStatement pst;
    private ResultSet rs;

    public TanamanPanel() {
        setLayout(new BorderLayout());

        // Panel untuk input data
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("ID"));
        txtID = new JTextField();
        txtID.setEditable(false); // ID tidak bisa diubah
        inputPanel.add(txtID);

        inputPanel.add(new JLabel("Nama"));
        txtNama = new JTextField();
        inputPanel.add(txtNama);

        inputPanel.add(new JLabel("Jenis"));
        txtJenis = new JTextField();
        inputPanel.add(txtJenis);

        inputPanel.add(new JLabel("Harga"));
        txtHarga = new JTextField();
        inputPanel.add(txtHarga);

        // Panel untuk tombol
        JPanel buttonPanel = new JPanel();
        btnSimpan = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnHapus = new JButton("Hapus");
        btnReset = new JButton("Reset");
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnReset);

        // Panel utama untuk menampilkan data
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        tableTanaman = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tableTanaman);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Koneksi ke database
        conn = dbConnect.connectDB();
        updateTable();

        // Action Listener untuk tombol Simpan
        btnSimpan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tambahData();
            }
        });

        // Action Listener untuk tombol Update
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                perbaruiData();
            }
        });

        // Action Listener untuk tombol Hapus
        btnHapus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hapusData();
            }
        });

        // Action Listener untuk tombol Reset
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetInputFields();
            }
        });

        // Mouse Listener untuk tabel (untuk mengisi form input)
        tableTanaman.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableTanaman.getSelectedRow();
                String id = tableTanaman.getModel().getValueAt(row, 0).toString();
                String nama = tableTanaman.getModel().getValueAt(row, 1).toString();
                String jenis = tableTanaman.getModel().getValueAt(row, 2).toString();
                String harga = tableTanaman.getModel().getValueAt(row, 3).toString();

                txtID.setText(id);
                txtNama.setText(nama);
                txtJenis.setText(jenis);
                txtHarga.setText(harga);
            }
        });
    }

    // Method untuk memperbarui data dalam tabel
    private void updateTable() {
        try {
            String sql = "SELECT id, nama, jenis, harga FROM tanaman";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            // Mengisi data ke tabel
            model.setColumnIdentifiers(new String[]{"ID", "Nama", "Jenis", "Harga"});
            model.setRowCount(0); // Menghapus data lama
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("nama"),
                        rs.getString("jenis"), rs.getDouble("harga")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Method untuk menambah data baru
    private void tambahData() {
        try {
            String sql = "INSERT INTO tanaman (nama, jenis, harga) VALUES (?, ?, ?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtNama.getText());
            pst.setString(2, txtJenis.getText());
            pst.setDouble(3, Double.parseDouble(txtHarga.getText()));
            pst.execute();
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
            updateTable();
            resetInputFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Method untuk memperbarui data yang sudah ada
    private void perbaruiData() {
        try {
            String sql = "UPDATE tanaman SET nama=?, jenis=?, harga=? WHERE id=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtNama.getText());
            pst.setString(2, txtJenis.getText());
            pst.setDouble(3, Double.parseDouble(txtHarga.getText()));
            pst.setInt(4, Integer.parseInt(txtID.getText()));
            pst.execute();
            JOptionPane.showMessageDialog(null, "Data berhasil diperbarui!");
            updateTable();
            resetInputFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Method untuk menghapus data
    private void hapusData() {
        int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin untuk menghapus data ini?", "Hapus Data", JOptionPane.YES_NO_OPTION);
        if (confirm == 0) {
            try {
                String sql = "DELETE FROM tanaman WHERE id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(txtID.getText()));
                pst.execute();
                JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
                updateTable();
                resetInputFields();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    // Method untuk mengosongkan field input
    private void resetInputFields() {
        txtID.setText("");
        txtNama.setText("");
        txtJenis.setText("");
        txtHarga.setText("");
    }

    // Main method untuk menjalankan aplikasi
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Aplikasi Pertanian");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new TanamanPanel());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
