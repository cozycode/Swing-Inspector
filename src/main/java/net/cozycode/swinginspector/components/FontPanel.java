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

package net.cozycode.swinginspector.components;

import static net.cozycode.constructs.Tuple.*;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.cozycode.constructs.IClosure;
import net.cozycode.constructs.Tuple2;
import net.cozycode.swing.components.LinkButton;
import net.cozycode.swing.formatters.FormattedComboBoxEditor;
import net.cozycode.swing.formatters.IntegerFormatter;
import net.cozycode.swing.text.FontUtilities;
import net.miginfocom.swing.MigLayout;

public class FontPanel extends JPanel {
   private static final long serialVersionUID = 1L;


   private final IClosure<Tuple2<FontPanel,Font>> closure;

   private final FontActionListener actionListener = new FontActionListener();
   private final JComboBox fontNames = new JComboBox( FontUtilities.getAvailableFontNames() );
   private final JComboBox fontSizes = new JComboBox( new Object[] {
      8, 10, 12, 14, 16, 18, 20
   });

   private JToggleButton bold = new JToggleButton( "B" );
   private JToggleButton italic = new JToggleButton( "I" );
   private JToggleButton underline = new JToggleButton( "U" );

   private final LinkButton nullButton = new LinkButton( "null" );

   private Font font;

   public FontPanel( Font font, IClosure<Tuple2<FontPanel,Font>> closure ) {
      this( closure, false );
      font = null;
   }

   public FontPanel( IClosure<Tuple2<FontPanel,Font>> closure, boolean nullable ) {
      super( new MigLayout( "insets 0", "", "[fill,grow]" ));
      this.closure = closure;

      fontNames.addActionListener( actionListener );
      fontSizes.addActionListener( actionListener );
      bold.addActionListener( actionListener );
      italic.addActionListener( actionListener );
      underline.addActionListener( actionListener );

      fontSizes.setEditor( new FormattedComboBoxEditor( new IntegerFormatter( false ), 3 ));
      fontSizes.setEditable( true );

      add( fontNames );
      add( fontSizes );
      add( bold, "newline, split 4");
      add( italic );
      add( underline );
      if( nullable ) {
         add( nullButton );
         nullButton.addActionListener( new NullListener() );
      }
   }

   public void setFontValue( Font font ) {
      this.font = font;

      fontNames.removeActionListener( actionListener );
      fontSizes.removeActionListener( actionListener );

      fontNames.setSelectedItem( font.getName() );
      fontSizes.setSelectedItem( font.getSize() );

      fontSizes.addActionListener( actionListener );
      fontNames.addActionListener( actionListener );

      bold.setSelected( font.isBold() );
      italic.setSelected( font.isItalic() );

      Object obj = font.getAttributes().get( TextAttribute.UNDERLINE );
      underline.setSelected( obj != null && (Integer)obj != -1 );
   }

   private final class FontActionListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         if( font == null ) return;

         if( fontNames == e.getSource() ) {
            Font f = FontUtilities.deriveFamily( font, (String)fontNames.getSelectedItem() );
            closure.yield( tuple( FontPanel.this, f ));
         }
         if( fontSizes == e.getSource() ) {
            Font f = FontUtilities.deriveSize( font, (Integer)fontSizes.getSelectedItem() );
            closure.yield( tuple( FontPanel.this, f ));
         }
         if( italic == e.getSource() ) {
            Float value = italic.isSelected() ? TextAttribute.POSTURE_OBLIQUE : null;
            Font f = FontUtilities.derivePosture( font, value );
            closure.yield( tuple( FontPanel.this, f ));
         }
         if( bold == e.getSource() ) {
            Float value = bold.isSelected() ? TextAttribute.WEIGHT_BOLD : null;
            Font f = FontUtilities.deriveWeight( font, value );
            closure.yield( tuple( FontPanel.this, f ));
         }
         if( underline == e.getSource() ) {
            Integer value = underline.isSelected() ? TextAttribute.UNDERLINE_ON : null;
            Font f = FontUtilities.deriveUnderline( font, value );
            closure.yield( tuple( FontPanel.this, f ));
         }
      }
   }

   private class NullListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         closure.yield( tuple( FontPanel.this, (Font)null ));
      } 
   }
}
