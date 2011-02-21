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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public final class SwingInspector {
   private SwingInspector() { /* Static Singleton */ }

   private static final SwingInspectorController controller = new SwingInspectorController();

   private static final int ALL_MODIFIERS = 
        InputEvent.SHIFT_DOWN_MASK 
      | InputEvent.CTRL_DOWN_MASK 
      | InputEvent.ALT_DOWN_MASK 
      | InputEvent.META_DOWN_MASK;


   /**
    * Installs an AWTEventListener which consumes all MouseEvents which match
    * the specified button and modifiers.  Instead, when that combination
    * occurs, the SwingInspector window is opened (if it is not open already)
    * and it's focused component is set to the component under the mouse
    * at the time of the event.<br />
    * <br />
    * It is important to note that, while the MouseEvent.KEY_PRESSED ID is 
    * the one which sets the focused component, the following MouseEvent IDs 
    * are consumed (MOUSE_CLICKED, MOUSE_PRESSED, and MOUSE_RELEASED).<br />
    * <br />
    * Internally this uses MouseEvent.getModifersEx() and not MouseEvent.getModifiers()
    * because the later can't distinguish between BUTTON2 and ALT or between BUTTON3 and META.<br />
    * <br />
    * 'modifiers' should be one or more of the following values bitwise ORed together: <br />
    * InputEvent.SHIFT_DOWN_MASK <br />
    * InputEvent.CTRL_DOWN_MASK <br />
    * InputEvent.ALT_DOWN_MASK <br />
    * InputEvent.META_DOWN_MASK <br />
    * <br />
    * Example: InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK
    * 
    * @param button - One of MouseEvent's button constants (ex: MouseEvent.BUTTON1)
    * @param modifiers - one of MouseEvent's modifierEx constants (ex: InputEvent.SHIFT_DOWN_MASK)
    */
   public static void installMouseListener( final int button, final int modifiers ) {
      Toolkit.getDefaultToolkit().addAWTEventListener( new AWTEventListener() {

         public void eventDispatched(AWTEvent event) {
            if( event instanceof MouseEvent ) {
               MouseEvent mouseEvent = ((MouseEvent)event);

               // WARNING: Do NOT use getModifiers() because BUTTON2_MASK and ALT_MASK are the same
               //          and BUTTON3_MASK and META_MASK are the same

               // NOTE: I found that it was safest to discard the button information of the modifiers
               //       by masking off everything but the SHIFT, CTRL, META and ALT modifiers.
               //       This is because unexpected modifier information was cropping up that
               //       had nothing to do with the modifier keys we care about here.

               boolean modifiersMatch = (mouseEvent.getModifiersEx() & ALL_MODIFIERS) == modifiers;

               if( mouseEvent.getButton() == button && modifiersMatch ) {
                  mouseEvent.consume(); 

                  if( MouseEvent.MOUSE_PRESSED == mouseEvent.getID() ) {
                     Component selected = SwingUtilities.getDeepestComponentAt( mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY() );
                     controller.inspectComponent( selected );
                  }
               }
            }
         }
      }, AWTEvent.MOUSE_EVENT_MASK );
   }

   /**
    * Installs an AWTEventListener which consumes all KeyEvents which match
    * the specified keycode and modifiers.  Instead, when that combination
    * occurs, the SwingInspector window is opened (if it is not open already)
    * and it's focused component is set to the component under the mouse
    * at the time of the event.<br />
    * <br />
    * It is important to note that, while the KeyEvent.KEY_PRESSED ID is 
    * the one which sets the focused component, all three KeyEvent IDs are
    * consumed (KEY_PRESSED, KEY_RELEASED, and KEY_TYPED).<br />
    * <br />
    * Internally this uses MouseEvent.getModifersEx() and not MouseEvent.getModifiers()
    * because the later can't distinguish between BUTTON2 and ALT or between BUTTON3 and META.<br />
    * <br />
    * 'modifiers' should be one or more of the following values bitwise ORed together: <br />
    * InputEvent.SHIFT_DOWN_MASK <br />
    * InputEvent.CTRL_DOWN_MASK <br />
    * InputEvent.ALT_DOWN_MASK <br />
    * InputEvent.META_DOWN_MASK <br />
    * <br />
    * Example: InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK
    * 
    * Note: if there are multiple swing frames under the mouse, the focused frame will
    *       be checked first.  However, after that the frames will be searched in random
    *       order as the swing API doesn't provide a way to determine top to bottom ordering.
    * 
    * @param keycode - one of KeyEvent's virtual key codes (ex: KeyEvent.VK_ENTER)
    * @param modifiers - one of KeyEvent's modifierEx constants (ex: KeyEvent.SHIFT_DOWN_MASK)
    */
   public static void installKeyListener( final int keycode, final int modifiers ) {
      AWTEventListener listener = new AWTEventListener() {

         public void eventDispatched(AWTEvent event) {
            if( event instanceof KeyEvent ) {
               KeyEvent keyEvent = (KeyEvent)event;

               // WARNING: Do NOT use getModifiers() because BUTTON2_MASK and ALT_MASK are the same
               //          and BUTTON3_MASK and META_MASK are the same

               // NOTE: I found that it was safest to discard the button information of the modifiers
               //       by masking off everything but the SHIFT, CTRL, META and ALT modifiers.
               //       This is because unexpected modifier information was cropping up that
               //       had nothing to do with the modifier keys we care about here.

               boolean modifiersMatch = (keyEvent.getModifiersEx() & ALL_MODIFIERS) == modifiers;


               if( keyEvent.getKeyCode() == keycode && modifiersMatch ) {
                  keyEvent.consume();

                  if( KeyEvent.KEY_PRESSED == keyEvent.getID() ) {
                     // TODO: Provide applet support, if necessary.

                     // Unfortunately, KeyEvent.getComponent() and KeyEvent.getSource()
                     // return the component which receives the KeyEvent which is
                     // the component with the focus, not the component under the mouse.
                     // That being the case, we will need to look up the component 
                     // under the mouse ourselves.

                     // First, try using the event component's root so that
                     // we can check the topmost window for a collision.
                     Component root = SwingUtilities.getRoot( keyEvent.getComponent() );
                     Point p = MouseInfo.getPointerInfo().getLocation();
                     SwingUtilities.convertPointFromScreen( p, root );
                     Component selected = SwingUtilities.getDeepestComponentAt( root, p.x, p.y );
                     if( selected != null ) {
                        controller.inspectComponent( selected );
                        return;
                     }


                     // This approach won't visit the windows in top to bottom order. 
                     // Thus, this may return a component on a window beneath another window.
                     // However, I wasn't able to find a way to resolve this.
                     for( Window window : Window.getWindows() ) {
                        p = MouseInfo.getPointerInfo().getLocation();
                        SwingUtilities.convertPointFromScreen( p, window );
                        selected = SwingUtilities.getDeepestComponentAt( window, p.x, p.y );

                        if( selected != null ) {
                           controller.inspectComponent( selected );
                           break;
                        }
                     }
                  }
               }
            }
         }
      };

      Toolkit.getDefaultToolkit().addAWTEventListener( listener, AWTEvent.KEY_EVENT_MASK );
   }
}
