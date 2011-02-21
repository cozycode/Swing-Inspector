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

import static net.cozycode.swinginspector.components.SwingInspectorUtilities.addTo;
import static net.cozycode.swinginspector.components.SwingInspectorUtilities.mildFont;
import static net.cozycode.swinginspector.components.SwingInspectorUtilities.strip;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.cozycode.core.IDisposable;
import net.cozycode.swing.components.JTitledSeparator;
import net.cozycode.swinginspector.Inspector;
import net.miginfocom.swing.MigLayout;


@Inspector(Object.class)
public class ObjectInspector extends JPanel implements IDisposable {
   private static final long serialVersionUID = 1L;
   private static final JCheckBox showPackages = strip( mildFont( new JCheckBox( "Show Packages", true )));
   private static final JCheckBox showSuperClasses = strip( mildFont( new JCheckBox( "Show Super Classes", true )));
   private static final JCheckBox showInterfaces= strip( mildFont( new JCheckBox( "Show Interfaces", true )));
   static { 
      showPackages.setComponentOrientation( ComponentOrientation.RIGHT_TO_LEFT );
      showSuperClasses.setComponentOrientation( ComponentOrientation.RIGHT_TO_LEFT );
      showInterfaces.setComponentOrientation( ComponentOrientation.RIGHT_TO_LEFT );
      showSuperClasses.setBorder( BorderFactory.createEmptyBorder( 5, 0, 5, 0 ));
   }

   private final Object obj;
   private final MyActionListener actionListener = new MyActionListener();

   public ObjectInspector( Object obj ) {
      super( new MigLayout( "wrap, fill", "[fill]" ));
      this.obj = obj;

      showPackages.addActionListener( actionListener );
      showSuperClasses.addActionListener( actionListener );
      showInterfaces.addActionListener( actionListener );

      recreateContent();
   }

   private void recreateContent() {
      removeAll();
      add( new JTitledSeparator( "Object" ));
      add( createContent( obj ));
   }

   private JComponent createContent( Object obj ) {
      Class<?> clazz = obj.getClass();
      Class<?> superclass = clazz.getSuperclass();

      JPanel pane = new JPanel( new MigLayout(  
            "wrap, fill, insets 0 10 0 0", 
            "[align right]10[grow]", "[align top][]" 
      ));

      pane.add( showPackages, "pos n 0 container.x2 n, id first" );
      pane.add( showSuperClasses, "pos n first.y2 container.x2 n, id second" );
      pane.add( showInterfaces, "pos n second.y2 container.x2 n" );

      addTo( pane, "Class:", getName( clazz ));

      // Add Super Classes
      String label = "Super Classes:";
      Class<?> iter = superclass;
      HashSet<Class<?>> interfaces = new HashSet<Class<?>>(); 
      while( iter != null ) {
         if( showSuperClasses.isSelected() ) {
            addTo( pane, label, getName( iter ));
         }

         for( Class<?> iface : iter.getInterfaces() ) {
            interfaces.add( iface );
         }

         label = "";
         iter = iter.getSuperclass();
      }

      // Add Interfaces
      if( showInterfaces.isSelected() ) {
         label = "Interfaces:";
         for( Class<?> iface : interfaces ) {
            addTo( pane, label, getName( iface ));
            label = "";
         }
      }

      return pane;
   }

   private String getName( Class<?> clazz ) {
      return showPackages.isSelected() 
         ? clazz.getName() 
         : clazz.getSimpleName();
   }

   private class MyActionListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         recreateContent();
      }
   }

   @Override
   public void dispose() {
      removeAll();
      showPackages.removeActionListener( actionListener );
      showSuperClasses.removeActionListener( actionListener );
      showInterfaces.removeActionListener( actionListener );
   }
}
