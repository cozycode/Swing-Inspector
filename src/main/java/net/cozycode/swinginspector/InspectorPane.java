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

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.cozycode.core.IDisposable;
import net.miginfocom.swing.MigLayout;


public class InspectorPane extends JPanel {
   private static final long serialVersionUID = 1L;
   private static final JComponent[] NO_INSPECTORS = new JComponent[0];

   private final InspectorFactory factory;

   private JComponent[] inspectors = NO_INSPECTORS;

   public InspectorPane( InspectorFactory factory ) {
      super( new MigLayout( "wrap 1, fillx, insets 5", "[fill]", "[pref]" ));
      this.factory = factory;
   }

   public void inspectComponent( Object obj ) {
      clearInspectors();
      createInspectors( obj );
      addInspectors();

      revalidate();
      repaint();
   }

   private void clearInspectors() {
      for( JComponent comp : inspectors ) {
         if( comp instanceof IDisposable ) {
            ((IDisposable)comp).dispose();
         }
      }
      inspectors = NO_INSPECTORS;
      removeAll();
   }

   private void createInspectors( Object comp ) {
      inspectors = comp != null
         ? factory.createInspectors( comp )
         : NO_INSPECTORS;
   }


   private void addInspectors() {
      for( JComponent inspector : inspectors ) {
         add( inspector );
      }
   }
}
