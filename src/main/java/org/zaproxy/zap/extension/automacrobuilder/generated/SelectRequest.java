/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zaproxy.zap.extension.automacrobuilder.generated;

import org.zaproxy.zap.extension.automacrobuilder.generated.ParmGenNew;
import java.awt.Point;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import org.zaproxy.zap.extension.automacrobuilder.PRequest;
import org.zaproxy.zap.extension.automacrobuilder.PRequestResponse;
import org.zaproxy.zap.extension.automacrobuilder.PResponse;
import org.zaproxy.zap.extension.automacrobuilder.ParmGenJSONSave;
import org.zaproxy.zap.extension.automacrobuilder.interfaceParmGenWin;

/**
 *
 * @author tms783
 */
@SuppressWarnings("serial")
public class SelectRequest extends javax.swing.JDialog {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("burp/Bundle");

       DefaultTableModel model;
       interfaceParmGenWin pgenwin;
       ArrayList<PRequestResponse> P_proxy_messages;
       int selected_message_idx;
       int panelno;
       interfaceParmGenWin nextwin;
       
    /**
     * Creates new form SelectRequest
     */
    public SelectRequest(String title, interfaceParmGenWin _pgenwin, interfaceParmGenWin _nextwin, int _panelno) {
        pgenwin = _pgenwin;
        panelno = _panelno;
        nextwin = _nextwin;
        setRequest(ParmGenJSONSave.proxy_messages);
        initComponents();
        setTitle(title);
        TableColumn col ;
        int[] colsize = {
            60, 250, 60
        };
        for(int i=0; i<3; i++){
            col= RequestTable.getColumnModel().getColumn(i);
            col.setPreferredWidth(colsize[i]);
        }
        this.setModal(true);
        switch(_panelno){
            case ParmGenNew.P_RESPONSETAB:
                selected_message_idx = 0;
                break;
            default:
                int size = RequestTable.getRowCount();
                if(size<=0){
                    size = 0;
                }else{
                    size--;
                }
                selected_message_idx = size;
                break;
        }
        RequestTable.setRowSelectionInterval(selected_message_idx, selected_message_idx);
        SwingUtilities.invokeLater(() -> {
            RequestTableMouseClicked(null);
        });
        

    }

    // Requestをセット
    // IHttpRequestResponse を引数にとるコンストラクタを作成。
    // setRequestに引数を渡す。
    public void setRequest(ArrayList <PRequestResponse> proxy_messages){
        
        model = new DefaultTableModel();

        P_proxy_messages = proxy_messages;

        // Create a couple of columns
        model.addColumn(bundle.getString("SelectRequest.METHOD.text"));
        model.addColumn(bundle.getString("SelectRequest.URL.text"));
        model.addColumn(bundle.getString("SelectRequest.STATUS.text"));
        
        if ( proxy_messages != null){
            for(int i=0; i< proxy_messages.size();i++){
                PRequest _request = proxy_messages.get(i).request;
                PResponse _response = proxy_messages.get(i).response;
                String method = _request.getMethod();
                String url = _request.getURL();
                String status = _response.getStatus();

                model.addRow(new Object[]{method, url, status});
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        RequestTable = new javax.swing.JTable();
        MessageSelected = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        RequestEntity = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ResponseEntity = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("SelectRequest.リクエスト選択.text")); // NOI18N

        RequestTable.setModel(model);
        RequestTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        RequestTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RequestTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(RequestTable);

        MessageSelected.setText(bundle.getString("SelectRequest.OK.text")); // NOI18N
        MessageSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MessageSelectedActionPerformed(evt);
            }
        });

        jButton2.setText(bundle.getString("SelectRequest.取消.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setText(bundle.getString("SelectRequest.リクエストを下記一覧から選択し、ＯＫボタンで選択。.text")); // NOI18N

        RequestEntity.setColumns(20);
        RequestEntity.setRows(5);
        RequestEntity.setText("POST http://tss-xxxxxxxxxxx? HTTP/1.1\nHost: xxxxxxxxxxxxxxxxxxxx\nUser-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; ja; rv:1.9.2.23) Gecko/20110920 Firefox/3.6.23 ( .NET CLR 3.5.30729; .NET4.0E)\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\nAccept-Language: ja,en-us;q=0.7,en;q=0.3\nAccept-Encoding: gzip,deflate\nAccept-Charset: Shift_JIS,utf-8;q=0.7,*;q=0.7\nKeep-Alive: 115\nProxy-Connection: keep-alive");
        jScrollPane2.setViewportView(RequestEntity);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("SelectRequest.REQUEST.text"), jPanel1); // NOI18N

        ResponseEntity.setColumns(20);
        ResponseEntity.setRows(5);
        jScrollPane3.setViewportView(ResponseEntity);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addGap(35, 35, 35))
        );

        jTabbedPane1.addTab(bundle.getString("SelectRequest.RESPONSE.text"), jPanel2); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane1)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1))
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(MessageSelected)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(MessageSelected)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void MessageSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MessageSelectedActionPerformed
        // TODO add your handling code here:
        // 選択メッセージを更新する。
        ParmGenJSONSave.selected_messages.clear();
        ParmGenJSONSave.selected_messages.add(ParmGenJSONSave.proxy_messages.get(selected_message_idx));
        pgenwin.updateMessageAreaInSelectedModel(panelno);
        dispose();
        if(nextwin!=null){
            nextwin.update();
            nextwin.setVisible(true);
        }
    }//GEN-LAST:event_MessageSelectedActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
       dispose();
          
    }//GEN-LAST:event_jButton2ActionPerformed

    private void RequestTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RequestTableMouseClicked
        // TODO add your handling code here:
        //Point point = evt.getPoint();
        //int row = RequestTable.rowAtPoint(point);
        //int column = RequestTable.columnAtPoint(point);
        //int[] sidx = RequestTable.getSelectedRows();
        int sidx = RequestTable.getSelectedRow();
        
        String _request = "";
        String _response = "";
        if ( sidx >= 0){
            _request = P_proxy_messages.get(sidx).request.getMessage();
            String ctype = P_proxy_messages.get(sidx).response.getContent_Type();
            if (ctype != null && ctype.startsWith("image")) {
                _response = P_proxy_messages.get(sidx).response.getHeaderOnly();
            }else{
                _response = P_proxy_messages.get(sidx).response.getMessage();
            }
            selected_message_idx = sidx;
        }
        SimpleAttributeSet attr = new SimpleAttributeSet();
        Document blank = new DefaultStyledDocument();
        Document qdoc = RequestEntity.getDocument();
        Document rdoc = ResponseEntity.getDocument();
        // RequestEntity.setDocument(blank);
        // ResponseEntity.setDocument(blank);
        //Document qdoc = new DefaultStyledDocument();
        //Document rdoc = new DefaultStyledDocument();
        

        try {
            qdoc.remove(0, qdoc.getLength());
            rdoc.remove(0, rdoc.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(SelectRequest.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            qdoc.insertString(0, _request, attr);
        } catch (BadLocationException ex) {
            Logger.getLogger(SelectRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            rdoc.insertString(0, _response, attr);
        } catch (BadLocationException ex) {
            Logger.getLogger(SelectRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        RequestEntity.setDocument(qdoc);
        ResponseEntity.setDocument(rdoc);
        //RequestEntity.setText(_request);
        //ResponseEntity.setText(_response);
        RequestEntity.setCaretPosition(0);
        ResponseEntity.setCaretPosition(0);
    }//GEN-LAST:event_RequestTableMouseClicked

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton MessageSelected;
    private javax.swing.JTextArea RequestEntity;
    private javax.swing.JTable RequestTable;
    private javax.swing.JTextArea ResponseEntity;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
