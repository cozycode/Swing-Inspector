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

package net.cozycode.swinginspector;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.cozycode.constructs.IClosure;
import net.cozycode.swinginspector.components.ComponentTree;
import net.cozycode.swinginspector.decorators.IComponentDecorator;
import net.miginfocom.swing.MigLayout;

public class SwingInspectorFrame extends JFrame {
   private static final long serialVersionUID = 1L;

   private final ArrayList<IClosure<IComponentDecorator>> decoratorChangedListeners = new ArrayList<IClosure<IComponentDecorator>>();
   private final TreeInspectionListener listener = new TreeInspectionListener();
   private final ComponentTree tree = new ComponentTree();
   private final InspectorPane inspectorPane;
   private final JComboBox decorations;

   public SwingInspectorFrame( Component inspecting, InspectorFactory factory, IComponentDecorator[] decorators ) {
      super( "Swing Inspector" );
      inspectorPane = new InspectorPane( factory );
      decorations = new JComboBox( decorators );
      decorations.addActionListener( new ActionListener(){
         @Override
         public void actionPerformed(ActionEvent e) {
            for( IClosure<IComponentDecorator> closure : decoratorChangedListeners ) {
               closure.yield( (IComponentDecorator)decorations.getSelectedItem() );
            }
         }
      });

      setPreferredSize( new Dimension( 900, 700 ));
      setContentPane( createContentPane() );

      inspectComponent( inspecting );
      tree.addInspectionListener( listener );
   }

   protected JComponent createContentPane() {
      JPanel panel = new JPanel( new BorderLayout() );

      JScrollPane treeScroller = new JScrollPane( tree );
      JScrollPane paneScroller = new JScrollPane( inspectorPane );

      treeScroller.setBorder( null );
      paneScroller.setBorder( null );

      JSplitPane splitter = new JSplitPane( 
         JSplitPane.HORIZONTAL_SPLIT,
         treeScroller,
         paneScroller
      );

      splitter.setDividerLocation( 250 );

      panel.add( createToolBar(), BorderLayout.NORTH );
      panel.add( splitter, BorderLayout.CENTER );
      return panel;
   }

   protected JComponent createToolBar() {
      JPanel panel = new JPanel( new MigLayout( "insets 3 10 3 5" ));
      panel.add( new JLabel( "Decoration" ));
      panel.add( decorations );
      return panel;
   }

   public void setDecorator( IComponentDecorator selected ) {
      decorations.setSelectedItem( selected );
   }

   public void inspectComponent( Component inspecting ) {
      tree.inspectComponent( inspecting );
      inspectorPane.inspectComponent( inspecting );
   }

   public void addInspectionListener( IInspectionListener listener ) {
      tree.addInspectionListener( listener );
   }

   public void removeInspectionListener( IInspectionListener listener ) {
      tree.removeInspectionListener( listener );
   }

   public void addDecoratorChangedListener( IClosure<IComponentDecorator> listener ) {
      decoratorChangedListeners.add( listener );
   }

   public void removeDecoratorChangedListener( IClosure<IComponentDecorator> listener ) {
      decoratorChangedListeners.remove( listener );
   }

   private final class TreeInspectionListener implements IInspectionListener {
      public void inspectionChanged(Component component) {
         inspectorPane.inspectComponent( component );
      }
   }
}
