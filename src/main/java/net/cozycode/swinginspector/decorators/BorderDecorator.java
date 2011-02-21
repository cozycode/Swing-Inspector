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

package net.cozycode.swinginspector.decorators;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

public class BorderDecorator implements IComponentDecorator {
   private static final String BORDER_KEY = "BorderDecorator.oldBorder";
   private static final String BORDER_PROPERTY = "border";
   private final BorderChangeListener listener = new BorderChangeListener();

   private int thickness;
   private Color color;

   public BorderDecorator( Color color, int thickness ) {
      this.color = color;
      this.thickness = thickness;
   }

   public void setColor( Color color ) {
      this.color = color;
   }

   public void setThickness( int thickness ) {
      this.thickness = thickness;
   }

   public void decorate(Component c) {
      if( c instanceof JComponent ) {
         JComponent jc = (JComponent)c;

         jc.putClientProperty( BORDER_KEY, jc.getBorder() );

         jc.setBorder( BorderFactory.createCompoundBorder(
               BorderFactory.createLineBorder( color, thickness ),
               jc.getBorder()
         ));

         c.addPropertyChangeListener( BORDER_PROPERTY, listener );
      }
   }

   public void undecorate(Component c) {

      if( c instanceof JComponent ) {
         JComponent jc = (JComponent)c;
         c.removePropertyChangeListener( BORDER_PROPERTY, listener );

         jc.setBorder( (Border)jc.getClientProperty( BORDER_KEY ));

         jc.putClientProperty( BORDER_KEY, null );
      }
   }

   private final class BorderChangeListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent evt) {
         if( evt.getSource() instanceof JComponent ) {
            JComponent comp = (JComponent)evt.getSource();

            comp.putClientProperty( BORDER_KEY, evt.getNewValue() );

            comp.removePropertyChangeListener( BORDER_PROPERTY, listener );
            comp.setBorder( BorderFactory.createCompoundBorder(
                  BorderFactory.createLineBorder( color, thickness ),
                  comp.getBorder()
            ));
            comp.addPropertyChangeListener( BORDER_PROPERTY, listener );

         }
      }
   }


   @Override
   public String toString() {
      return "Border";
   }
}
