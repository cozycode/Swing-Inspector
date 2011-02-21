/*
 * Copyright (C) 2011 Cozycode.net
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cozycode.swinginspector.inspectors;

import static net.cozycode.swinginspector.components.SwingInspectorUtilities.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.cozycode.core.IDisposable;
import net.cozycode.swing.components.JTitledSeparator;
import net.cozycode.swinginspector.Inspector;
import net.miginfocom.swing.MigLayout;


/* POSSIBLE EXTENSIONS
 * -------------------
 * InputMap
 * toolTipText
 * visibleRect
 * clientProperties
 */
@Inspector(JComponent.class)
public class JComponentInspector extends JPanel implements IDisposable {
   private static final long serialVersionUID = 1L;

   private final MyPropertyChangeListener propertyListener = new MyPropertyChangeListener();
   private final FieldActionListener actionListener = new FieldActionListener();

   private final JLabel uiClassId = new JLabel();
   private final JLabel borderClass = new JLabel();

   private final JCheckBox autoscrolls = new JCheckBox();

   private final JComponent comp;

   public JComponentInspector( JComponent comp ) {
      super( new MigLayout( "wrap,fill", "[fill]" ));
      this.comp = comp;

      comp.addPropertyChangeListener( propertyListener );
      autoscrolls.addActionListener( actionListener );

      add( new JTitledSeparator( "JComponent" ));
      add( createContent() );
      updateValues();
   }

   @Override
   public void dispose() {
      comp.removePropertyChangeListener( propertyListener );
   }

   private JComponent createContent() {
      JPanel pane = new JPanel( new MigLayout(  
         "wrap, fill, insets 0 10 0 0", 
         "[align right]6[grow,fill]",
         "[align top]"
      ));

      addTo( pane, "UI Class ID:", uiClassId );
      addTo( pane, "Border Class:", borderClass );
      addSpacer( pane );

      addTo( pane, "Autoscrolls:", autoscrolls );
      addSpacer( pane );

      return pane;
   }

   private void updateValues() {
      Border border = comp.getBorder();

      uiClassId.setText( comp.getUIClassID() );
      borderClass.setText( border != null ? border.getClass().getName() : "null" );
      autoscrolls.setSelected( comp.getAutoscrolls() );
   }

   private final class FieldActionListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         if( autoscrolls == e.getSource() ) {
            comp.setAutoscrolls( autoscrolls.isSelected() );
         }
      }
   }

   private final class MyPropertyChangeListener implements PropertyChangeListener{
      public void propertyChange(PropertyChangeEvent evt) {
         updateValues();
      }
   }
}
