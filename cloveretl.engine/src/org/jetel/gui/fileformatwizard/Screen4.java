/*
 *  jETeL/Clover - Java based ETL application framework.
 *  Copyright (C) 2003,2002  David Pavlis, Wes Maciorowski
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.jetel.gui.fileformatwizard;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextPane;

import org.jetel.gui.component.FormInterface;
import java.awt.Font;

public class Screen4 extends JPanel implements  FormInterface
{
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel jLabel1 = new JLabel();
  private JTextPane jTextPane1 = new JTextPane();
  private JLabel jLabel2 = new JLabel();
	 
  private FileFormatDispatcher aDispatcher;
  private FileFormatDataModel aFileFormatParameters;
	
  
  public Screen4(FileFormatDispatcher aDispatcher, FileFormatDataModel aFileFormatParameters)
  {
	this.aDispatcher = aDispatcher;
	this.aFileFormatParameters = aFileFormatParameters;
  	
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }

  public Screen4()
  {
	try
	{
	  jbInit();
	}
	catch(Exception e)
	{
	  e.printStackTrace();
	}

  }

  public static void main(String[] args)
  {
    Screen4 screen4 = new Screen4();
  }

  private void jbInit() throws Exception
  {
    this.setLayout(gridBagLayout1);
    jLabel1.setText("Screen 4 of 4");
    jLabel1.setFont(new Font("Dialog", 1, 11));
    jTextPane1.setText("jTextPane1");
    jLabel2.setText(" Preview Final XML");
    this.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    this.add(jTextPane1, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    this.add(jLabel2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
  }

/* (non-Javadoc)
 * @see org.jetel.gui.component.PhasedPanelInterface#validateData()
 */
public boolean validateData() {
	// TODO Auto-generated method stub
	return false;
}

/* (non-Javadoc)
 * @see org.jetel.gui.component.PhasedPanelInterface#saveData()
 */
public void saveData() {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see org.jetel.gui.component.FormInterface#loadData()
 */
public void loadData() {
	// TODO Auto-generated method stub
	
}
}