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

import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.cozycode.core.IDisposable;
import net.cozycode.swing.components.BooleanLabel;
import net.cozycode.swing.components.JTitledSeparator;
import net.cozycode.swinginspector.Inspector;
import net.cozycode.swinginspector.components.InsetsPanel;
import net.miginfocom.swing.MigLayout;

@Inspector(Container.class)
public class ContainerInspector extends JPanel implements IDisposable {
   private static final long serialVersionUID = 1L;

   private final MyContainerListener containerListener = new MyContainerListener();
   private final MyPropertyChangeListener propertyListener = new MyPropertyChangeListener();

   private final JLabel layoutClass = new JLabel();
   private final JLabel componentCount = new JLabel();

   private final InsetsPanel insetsPanel = new InsetsPanel();

   private final BooleanLabel isFocusCycleRoot = new BooleanLabel();
   private final BooleanLabel isFocusTraversalPolicySet = new BooleanLabel();
   private final BooleanLabel isFocusTraversalPolicyProvider = new BooleanLabel();

   private final Container container;

   public ContainerInspector( Container container ) {
      super( new MigLayout( "wrap,fill", "[fill]" ));
      this.container = container;

      container.addContainerListener( containerListener );
      container.addPropertyChangeListener( propertyListener );

      add( new JTitledSeparator( "Container" ));
      add( createContent() );
      updateValues();
   }

   @Override
   public void dispose() {
      container.removePropertyChangeListener( propertyListener );
      container.removeContainerListener( containerListener );
   }

   private JComponent createContent() {
      JPanel pane = new JPanel( new MigLayout(  
         "wrap, fill, insets 0 10 0 0", 
         "[align right]6[grow,fill]",
         "[align top]"
      ));

      addTo( pane, "Layout Class:", layoutClass );
      addTo( pane, "Component Count:", componentCount );
      addSpacer( pane );

      addTo( pane, "Insets:", insetsPanel );
      addSpacer( pane );

      addTo( pane, "Is Focus Cycle Root:", isFocusCycleRoot );
      addTo( pane, "Is Focus Traversal Policy Set:", isFocusTraversalPolicySet );
      addTo( pane, "Is Focus Traversal Policy Provider:", isFocusTraversalPolicyProvider );
      addSpacer( pane );

      return pane;
   }

   private void updateValues() {
      LayoutManager layout = container.getLayout();

      layoutClass.setText( layout != null ? layout.getClass().getName() : "null" );
      componentCount.setText( ""+ container.getComponentCount() );

      insetsPanel.setInsets( container.getInsets() );

      isFocusCycleRoot.setBoolean( container.isFocusCycleRoot() );
      isFocusTraversalPolicySet.setBoolean( container.isFocusTraversalPolicySet() );
      isFocusTraversalPolicyProvider.setBoolean( container.isFocusTraversalPolicyProvider() );

   }

   private final class MyContainerListener implements ContainerListener {
      @Override
      public void componentAdded(ContainerEvent e) {
         updateValues();
      }

      @Override
      public void componentRemoved(ContainerEvent e) {
         updateValues();
      }
   }

   private final class MyPropertyChangeListener implements PropertyChangeListener{
      public void propertyChange(PropertyChangeEvent evt) {
         updateValues();
      }
   }
}
