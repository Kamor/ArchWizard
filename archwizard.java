// archwizard - v0.0 - kamor - 22.09.2022
// java has no delete object / java uses garbage collector / setting object to null, will speed up garbage collection / java can clear arraylists
// ArrayList can not handle <int>, we need <Integer>
import java.util.ArrayList;

import java.lang.Thread;

// using io or nio ??? todo https://www.baeldung.com/java-path-vs-file
import java.io.File; // IO before Java 7
import java.nio.file.Path; // NIO available from Java 7 onward
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.FileReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Insets;	// used for margin in textfield

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import javax.swing.JFrame;
	
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import javax.swing.table.AbstractTableModel;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;

public class ArchWizard
{
	// static main must be in first class, later this could be split to different textfiles
	public static void main(String[] args) // throws InterruptedException
	{
		// Thread.sleep(3000);
		
		// System.out.println("Working Directory = " + System.getProperty("user.dir"));
		// System.out.println("Home Directory = " + System.getProperty("user.home"));
		
		// we try multi threading later
		// MyThread t = new MyThread();
    // t.start();
	
		ArchFrame archframe = new ArchFrame();
	}
}

/*public class MyThread extends Thread
{
	public void run()
  {
		try
		{
			System.out.println("Thread " + Thread.currentThread().getId() + " is running");
		}
		catch (Exception e) {System.out.println("Exception is caught");
		}
	}
}*/

public class Pair
{
	String name;
	String value;
}

public class XObject
{
	String name; // unique name of object, it's object for archetypes and artifact for artifacts
	String file; // path where object definition comes from
	ArrayList<Pair> attributeList = new ArrayList<Pair>();
	boolean changed=false; // default
}

// ----------------------------------------------------
// XData
// ----------------------------------------------------
// this is the real data class, at start or on reset all archetype files and artifact file are read in this objectList
// TODO sort functions to there logical classes
public class XData
{
	ArrayList<XObject>objectList = new ArrayList<XObject>();
	ArrayList<String>attributes = new ArrayList<String>(); // attributenames in alphabetic order, we found on init
	boolean changed=false; // default

	int size()
	{
		return objectList.size();
	}
	
	boolean set(int index, String attribute, String value)
	{
		// we don't check for illegal index <0, we only check for index>=max
		if (index>=objectList.size())
		{
			return false;
		}
		
		XObject xObject=objectList.get(index); //reference
		ArrayList<Pair> object = xObject.attributeList;
		
		// search for the attribute name
		for (int i=0;i<object.size();i++)
		{
			if (attribute.equals(object.get(i).name)) // found the attribute name
			{
				// when we use "null" we want to delete the attribute
				if (value.equals("null"))
				{
					object.remove(i);
				}
				else
				{
					if (value.equals(object.get(i).value)) return true; // no change
					// write new attribute value
					Pair pair=object.get(i);
					pair.value=value;
					object.set(i,pair);
				}
				
				xObject.changed=true;
				changed=true;
				return true;
			}
		}
		
		// here we didn't found the attribute name
		
		// if (value==null) return true; // no need to delete an attribute, when attribute not exists
		if (value.equals("null")) return true; // no need to delete an attribute, when attribute not exists
		
		// add new attribute before end attribute // TODO could be sorted better
		for (int i=object.size()-1;i>=0;i--)
		{
			if (object.get(i).name.equals("end"));
			{
				Pair pair= new Pair();
				pair.name=attribute;
				pair.value=value;
				object.add(i,pair);
				xObject.changed=true;
				changed=true;
				return true;
			}
		}
		return false;
	}
	
	int find(String name)
	{
		for (int i=0;i<objectList.size();i++)
		{
			if (name.equals(objectList.get(i).name))
			{
				return i;
			}
		}
		return -1; // return -1 if not found
	}
	
	String get(int index, String attribute)
	{
		// we don't check for illegal index <0, we only check for index>=max
		if (index>=objectList.size())
		{
			return "?index"; // TODO improve logic, to reduce possible errors like this
		}
		XObject xObject=objectList.get(index); //reference
		ArrayList<Pair> object = xObject.attributeList;
		
		for (int i=0;i<object.size();i++)
		{
			if (attribute.equals(object.get(i).name))
			{
				return object.get(i).value;
			}
		}
		return "null";
	}
	
	// id object or arch, so later we can found this faster, this logic must be improved more
	// this is backside, we have put archetypes and artifacts in same list, we always must look, what object we have
	String idXObject(ArrayList<Pair> object)
	{
		for (int j=0; j<object.size();j++)
		{
			Pair attribute=object.get(j);

			if (attribute.name.equals("Object"))
			{
				// if we have a definition for Object this is from archetypes
				if (!attribute.value.equals(""))
				{
					return "Object "+attribute.value;
				}
			}
					
			if (attribute.name.equals("artifact"))
			{
				// if we have a definition for artifact this is from artifacts
				if (!attribute.value.equals(""))
				{
					return "artifact "+attribute.value;
				}
			}
		} //for
		return ""; // i think better than return null
	}
	
	// ----------------------------------------------------
	// addAttributes
	// ----------------------------------------------------	
	// we check, if we have this attributes in our list, if not we append it
	// we also sort it alphabetic
	void addAttributes(ArrayList<Pair> object)
	{
		for (int j = 0; j < object.size(); j++)
		{
			Pair pair=object.get(j);
				
			// we search for attribute if we have this in our list and we also compare where we want it inside our list	
			boolean found=false;
			int insertIndex=0;
			for (int k=0; k<attributes.size(); k++)
			{
				if (pair.name.equals(attributes.get(k)))
				{
					// we have this attribute stored before from other object, no need to continue search
					found=true;
					break;
				}
				
				if (attributes.get(k).compareTo(pair.name)<0)
				{
					insertIndex++;
				}
			} // for
			if (!found)
			{
				attributes.add(insertIndex, pair.name); // storing a String is not a reference, so we have a clean copy
			}
		} // for
	}
}

// ----------------------------------------------------
// XFilter
// ----------------------------------------------------
// we have 2 filters here
// showObjectList is builded when using search commands
// attribute filter todo
public class XFilter
{
	XData xData; // xFilter need a reference to xData
	
	ArrayList<Integer>showObjectList = new ArrayList<Integer>(); // index to objectList for search filter (filters full objects)
	ArrayList<String>showAttributList = new ArrayList<String>(); // attributeList for search filter (filters attribute)
	
	boolean filterColumns=false;
	boolean filterRows=false;
	
	
	// public ArrayList<String> WhiteList = new ArrayList<ArrayList<String>>();
	// public ArrayList<String> BlackList = new ArrayList<ArrayList<String>>();
	
	XFilter(XData _xData)
	{
		xData=_xData; // we have reference to xData now
	}
	
	void setFilter(ArrayList<String> filterList)
	{
		showAttributList.clear();
		showAttributList=filterList;
	}

	// we don't use filter flag on searched data, only search commands change this here
	int size()
	{
		if (filterRows)
		{
			return showObjectList.size();
		}
		else
		{
			return xData.size();		
		}
	}
	
	public String get(int index, String attribute)
	{
		if (filterRows)
		{
			index=showObjectList.get(index);
		}
		return xData.get(index, attribute); // error handling is in xData
	}
	
	boolean set(int index, String attribute, String value)
	{
		if (filterRows)
		{
			index=showObjectList.get(index);
		}
		return xData.set(index, attribute, value); // error handling is in xData
	}
	
	// todo this is still very ugly
	int getColumnCount()
	{
		if (filterColumns)
		{
			return showAttributList.size();
		}
		return xData.attributes.size();
  }
	
	// todo this is still very ugly
	String getColumnName(int columnIndex)
		{
			if (filterColumns)
			{
				return showAttributList.get(columnIndex);
			}
			return xData.attributes.get(columnIndex);
		}
	
	// ----------------------------------------------------
	// addColumns (headers) - attribute names
	// ----------------------------------------------------	
	// we check, if we have this headers in our list, if not we append it
	// we store the index to xData.attributes
	void addColumns(ArrayList<Pair> object)
	{
		for (int j = 0; j < object.size(); j++)
		{
			Pair pair=object.get(j);
				
			boolean found=false;
			for (int k=0; k<showAttributList.size(); k++)
			{
				if (pair.name.equals(showAttributList.get(k)))
				{
					// we have this attribute stored before from other object, no need to continue search
					found=true;
					break;
				}
			} // for
			
			if (!found)
			{
				showAttributList.add(pair.name); // storing a String is not a reference
				// todo this is old logic, didn't sort <--------------------------
			}
		} // for
	}
}

// ----------------------------------------------------
// XTableModel
// ----------------------------------------------------
// this is a virtual table now, data comes from XData
// here we do the conservation to JTable
// todo optimize filter logic, current logic produces hard to find errors when something is wrong
// todo we need unfiltered and filtered columns here
// problem we build on search mode a kind of prefilter in columnslist and use filter to filter this
// we need to rethink this, columnslist should have the info of full data, for import logic
// so we can communicate with full data or filtered data

public class XTableModel extends AbstractTableModel
{
		XFilter xFilter;

		public XTableModel(XFilter _xFilter)
		{
			xFilter=_xFilter; // we have reference to xFilter now
		}
		
		public void update()
		{
			fireTableStructureChanged();		
		}
		
		@Override
    public int getRowCount()
		{
			return xFilter.size();
		}

    @Override
    public int getColumnCount()
		{
			return xFilter.getColumnCount();
	  }
		
		@Override
    public Object getValueAt(int rowIndex, int columnIndex)
		{
			return xFilter.get(rowIndex, xFilter.getColumnName(columnIndex)); // todo ugly
    }
		
		@Override
		public String getColumnName(int columnIndex)
		{
			return xFilter.getColumnName(columnIndex);
		}
		
		@Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return (true);
    }
		
		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			String sOld=xFilter.get(rowIndex, xFilter.getColumnName(columnIndex)); // ugly
			String sNew=(String)value;
			
			if (sOld.equals(sNew)) return; // no change
			xFilter.set(rowIndex, xFilter.getColumnName(columnIndex), (String)value); // ugly
			fireTableCellUpdated(rowIndex, columnIndex); // todo firetable is used for show changes with @edit cells command
			// todo how time expensive is this to fireTable for a cell?
		}
}

// ----------------------------------------------------
// ArchFrame
// ----------------------------------------------------
// we can only extend one class, here we choosed JFrame, so we can't extend KeyAdapter, we need implement Listener
// KeyListener is always in relation to keyboard focus, so we need implement multiply listeners on each gui with a possible keyboard input
public class ArchFrame extends JFrame implements ActionListener, ListSelectionListener
{
	final String VERSION="archwizard - v0.0 - kamor - 22.09.2022";
	final String CONFIG="archwizard.ini";
	final String HELP="archwizard.txt";
	final String PROTO="proto.arc";

	String userDir=""; // we read this at program start in constructor, this is always where archwizard.java is started
	
	String daimoninDir=""; // this comes from CONFIG
	String archDir=""; // this should come from CONFIG to, currently it's builded in init
	String libDir=""; // same

	String sOut = ""; // output string, because we currently use one textarea, we can always toggle back to older output
	String sLog = ""; // primitive log string, use memory, is lost on program termination
	String sError = ""; // for logging errors in data
	String sWarning = ""; // for logging warnings in data
	String sexport = ""; // we build .csv here

	boolean bLog=true;	// flag to switch on/off sLog
	
	int doXMode=0; // nothing
	final int doXModeReset=1;
	final int doXModeCollect=2;
	final int doXModeBackup=3;
	final int doXModeRestore=4;
	final int doXModeDelete=5;
	
	int cError=0;
	int cWarning=0;
	
	XData xData=new XData();
	XFilter xFilter=new XFilter(xData);

	ArrayList<String>filterSelectedList = new ArrayList<String>();

	ArrayList<Pair> objectPathList = new ArrayList<Pair>(); // here we store the filepath for an object
	ArrayList<Pair> pngList = new ArrayList<Pair>();
	ArrayList<Pair> animList = new ArrayList<Pair>();
	
	ArrayList<Pair> protoList = new ArrayList<Pair>();
	ArrayList<Pair> configList = new ArrayList<Pair>();

	DefaultListModel<String> listModel = new DefaultListModel<>(); // we can add, delete list entrys here
	JList<String> jList = new JList<String>(listModel); // here we handle the selection
	JScrollPane filterscrollpane = new JScrollPane (jList); // here we toogle filter on/off and handle long list with scrollbars
	
	int counterArchetypes=0;
	int counterArtifacts=0;
	int objectPointer=0;
	
	boolean working=true; // flag to wait for longer processes 
	boolean filter=false; // flag to know if filter is toggled on/off
	
	String currentFilename=""; // if textarea show content of a file, here is the name, i hope
	
	// global gui objects
  JFrame frame;
	JTextField cmdField;
	JLabel statusLabel;
	
	JTabbedPane tabbedPane;
	JTextArea textArea;
	JTable jTable;
	XTableModel model;

	// ----------------------------------------------------
	// list selection listener
	// ----------------------------------------------------	
	// list selection listener is calling this
	// logic rebuild list and show table each click
	public void valueChanged(ListSelectionEvent e)
	{
		// clicking in jlist triggers 2 times, we only react when e.isAdjusting is false
		if (e.getValueIsAdjusting()==true)
		{
			return;
		}
		// there is no need to show nothing, when nothing is selected, we show everything (toggle filter off).
		// so select all shows same than select nothing
		if (jList.isSelectionEmpty())
		{
			xFilter.filterColumns=false;
			model.update();
			return;
		}
		// we grap all selected entrys and send this to the filter
		// so each click/change in jlist will show then changes in our table
		ArrayList<String>	selectedHeaders=new ArrayList<String>();
		for (int i = jList.getMinSelectionIndex(); i <= jList.getMaxSelectionIndex(); i++)
		{
			if (jList.isSelectedIndex(i))
			{
				selectedHeaders.add(jList.getModel().getElementAt(i));
				// System.out.println(jList.getModel().getElementAt(i));
			}
		}
		xFilter.setFilter(selectedHeaders);
		xFilter.filterColumns=true;
		model.update();
	} 
	
	public class TastEvent extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{ 
      if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			{ 
				System.exit(0);
			}
			
			if (e.isControlDown())
			{
				if (e.getKeyCode() == KeyEvent.VK_L)
				{
					actionX("@load");
				}
				if (e.getKeyCode() == KeyEvent.VK_S)
				{
					actionX("@save");
				}
				if (e.getKeyCode() == KeyEvent.VK_F)
				{
					actionX("@filter");
				}
				if (e.getKeyCode() == KeyEvent.VK_E)
				{
					actionX("@editor");
				}
				if (e.getKeyCode() == KeyEvent.VK_W)
				{
					actionX("@write");
				}
				
				if (e.getKeyCode() == KeyEvent.VK_PLUS)
				{
					actionX("@+");
				}
				if (e.getKeyCode() == KeyEvent.VK_MINUS)
				{
					actionX("@-");
				}
				
				if (e.getKeyCode() == KeyEvent.VK_LESS)
				{
					actionX("@search");
				}

				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					cmdField.requestFocus();
				}
				
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
				{
					cmdField.setText("");
					cmdField.requestFocus();
				}
				
				if (e.getKeyCode() == KeyEvent.VK_O)
				{
					cmdField.setText("Object ");
					cmdField.requestFocus();
				}
			}	
		}
	}
 
	// ----------------------------------------------------
	// ArchFrame - Constructor
	// ----------------------------------------------------
	// build gui
	ArchFrame()
  {
		userDir = System.getProperty("user.dir");
		
		frame = new JFrame("ArchWizard");
		frame.setSize(800, 640);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // terminate prg, without this we need str+c to stop
			
		// --- menu ---	
		JMenuBar menuBar = new JMenuBar();
		
		JMenu m_file = new JMenu("File");
		
		JMenuItem m_file_load = new JMenuItem("@load");
		m_file_load.addActionListener(this);
		m_file.add(m_file_load);
		
		m_file.addSeparator();
		
		JMenuItem m_file_animations = new JMenuItem("@load animations");
		m_file_animations.addActionListener(this);
		m_file.add(m_file_animations);
		
		JMenuItem m_file_archetypes = new JMenuItem("@load archetypes");
		m_file_archetypes.addActionListener(this);
		m_file.add(m_file_archetypes);
		
		JMenuItem m_file_artifacts = new JMenuItem("@load artifacts");
		m_file_artifacts.addActionListener(this);
		m_file.add(m_file_artifacts);
		
		JMenuItem m_file_bmaps = new JMenuItem("@load bmaps");
		m_file_bmaps.addActionListener(this);
		m_file.add(m_file_bmaps);
		
		m_file.addSeparator();
		
		JMenuItem m_file_save = new JMenuItem("@save");
		m_file_save.addActionListener(this);
		m_file.add(m_file_save);
		
		m_file.addSeparator();
		
		JMenuItem m_export = new JMenuItem("@export");
		m_export.addActionListener(this);
		m_file.add(m_export);
		
		m_file.addSeparator();
		
		JMenuItem m_import = new JMenuItem("@import");
		m_import.addActionListener(this);
		m_file.add(m_import);
						
		menuBar.add(m_file);
		
		// ---
		
		JMenu m_data = new JMenu("Data");
		
		JMenuItem mi_write = new JMenuItem("@write");
		mi_write.addActionListener(this);
		m_data.add(mi_write);
		
		JMenuItem mi_show = new JMenuItem("@show");
		mi_show.addActionListener(this);
		m_data.add(mi_show);
		
		menuBar.add(m_data);
		
		// ---
		
		JMenu m_view = new JMenu("View");
		
		JMenuItem mi_filter = new JMenuItem("@filter");
		mi_filter.addActionListener(this);
		m_view.add(mi_filter);
		
		menuBar.add(m_view);
		
		// --
		
		JMenu m_extern = new JMenu("Extern");
		
		JMenuItem mi_editor = new JMenuItem("@editor");
		mi_editor.addActionListener(this);
		m_extern.add(mi_editor);
		
		menuBar.add(m_extern);
		
		// --
		
		JMenu m_config = new JMenu("Config");
		
		JMenuItem mi_proto = new JMenuItem("@load proto");
		mi_proto.addActionListener(this);
		m_config.add(mi_proto);
		
		JMenuItem mi_edit_config = new JMenuItem("@load config");
		mi_edit_config.addActionListener(this);
		m_config.add(mi_edit_config);
		
		m_config.addSeparator();
		
		JMenuItem mi_set_path = new JMenuItem("@set path");
		mi_set_path.addActionListener(this);
		m_config.add(mi_set_path);
		
		menuBar.add(m_config);
		
		// ---
		
		JMenu m_clean = new JMenu("Clean");
		
		JMenuItem mi_trim = new JMenuItem("@trim this");
		mi_trim.addActionListener(this);
		m_clean.add(mi_trim);
		
		menuBar.add(m_clean);
		
		// ---
		
		JMenu m_collect = new JMenu("Collect");
		
		JMenuItem mi_collect_archetypes = new JMenuItem("@collect archetypes");
		mi_collect_archetypes.addActionListener(this);
		m_collect.add(mi_collect_archetypes);
		
		menuBar.add(m_collect);
		
		// ---
		
		JMenu m_backup = new JMenu("Backup");
		
		JMenuItem mi_backup = new JMenuItem("@backup");
		mi_backup.addActionListener(this);
		m_backup.add(mi_backup);
		
		JMenuItem mi_restore = new JMenuItem("@restore");
		mi_restore.addActionListener(this);
		m_backup.add(mi_restore);
		
		JMenuItem mi_delete = new JMenuItem("@delete");
		mi_delete.addActionListener(this);
		m_backup.add(mi_delete);
		
		menuBar.add(m_backup);
		
		// ---
		
		JMenu m_debug = new JMenu("Debug");
		
		JMenuItem mi_out = new JMenuItem("@out");
		mi_out.addActionListener(this);
		m_debug.add(mi_out);
		
		JMenuItem mi_log = new JMenuItem("@log");
		mi_log.addActionListener(this);
		m_debug.add(mi_log);
		
		JMenuItem mi_warning = new JMenuItem("@warning");
		mi_warning.addActionListener(this);
		m_debug.add(mi_warning);
		
		JMenuItem mi_error = new JMenuItem("@error");
		mi_error.addActionListener(this);
		m_debug.add(mi_error);
		
		m_debug.addSeparator();
		
		JMenuItem mi_configlist = new JMenuItem("@configlist");
		mi_configlist.addActionListener(this);
		m_debug.add(mi_configlist);
		
		JMenuItem mi_protolist = new JMenuItem("@protolist");
		mi_protolist.addActionListener(this);
		m_debug.add(mi_protolist);
		
		JMenuItem mi_attributes = new JMenuItem("@attributes");
		mi_attributes.addActionListener(this);
		m_debug.add(mi_attributes);
		
		m_debug.addSeparator();
		
		JMenuItem mi_png = new JMenuItem("@pnglist");
		mi_png.addActionListener(this);
		m_debug.add(mi_png);
		
		JMenuItem mi_arc = new JMenuItem("@objectpathlist");
		mi_arc.addActionListener(this);
		m_debug.add(mi_arc);
		
		JMenuItem mi_anim = new JMenuItem("@animlist");
		mi_anim.addActionListener(this);
		m_debug.add(mi_anim);
		
		m_debug.addSeparator();
		
		JMenuItem mi_init = new JMenuItem("@reset");
		mi_init.addActionListener(this);
		m_debug.add(mi_init);
		
		menuBar.add(m_debug);
		
		// ---
		
		JMenu m_info = new JMenu("Info");
		
		JMenuItem mi_info = new JMenuItem("@info");
		mi_info.addActionListener(this);
		m_info.add(mi_info);
		
		JMenuItem mi_help = new JMenuItem("@load help");
		mi_help.addActionListener(this);
		m_info.add(mi_help);
		
		menuBar.add(m_info);

		frame.setJMenuBar(menuBar);
 
		// --- cmdfield ---
		
		JPanel cmdPanel = new JPanel(new BorderLayout());
		cmdPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		cmdField = new JTextField();
		cmdField.setMargin(new Insets(2, 2, 4, 2));
		cmdField.addActionListener(this);
		
		cmdPanel.add(cmdField, BorderLayout.NORTH); //, BorderLayout.CENTER);
		
		// JPanel checkPanel = new JPanel();
		//JCheckBox checkBoxArchetypes = new JCheckBox("Archetypes");
    //JCheckBox checkBoxArtifacts = new JCheckBox("Artifacts");
		//checkPanel.add(checkBoxArchetypes);
		//checkPanel.add(checkBoxArtifacts);
		
		//cmdPanel.add(checkPanel, BorderLayout.SOUTH); //, BorderLayout.CENTER);
		
		frame.add(cmdPanel, BorderLayout.NORTH);
		
		// --- statusbar ---
		
		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		frame.add(statusPanel, BorderLayout.SOUTH);
			
		statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 32));

		statusLabel = new JLabel("Status");
		
		statusPanel.add(statusLabel);
		
		// --- textarea ---
		
		textArea = new JTextArea();
		textArea.setMargin(new Insets(2, 2, 2, 2));
		
		// --- popmenu right mouse button ---
		
		JPopupMenu pop_menu = new JPopupMenu("Popup"); 
		
		JMenuItem pop_search = new JMenuItem("@search");
		pop_search.addActionListener(this);
		pop_menu.add(pop_search);
		
		pop_menu.addSeparator();
		
		JMenuItem pop_save = new JMenuItem("@save");
		pop_save.addActionListener(this);
		pop_menu.add(pop_save);
		
		pop_menu.addSeparator();
		
		JMenuItem pop_load = new JMenuItem("@load");
		pop_load.addActionListener(this);
		pop_menu.add(pop_load);
		
		pop_menu.addSeparator();
		
		JMenuItem pop_editor = new JMenuItem("@editor");
		pop_editor.addActionListener(this);
		pop_menu.add(pop_editor);
		
		pop_menu.addSeparator();
		
		JMenuItem pop_export = new JMenuItem("@export");
		pop_export.addActionListener(this);
		pop_menu.add(pop_export);
		
		textArea.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				//right mouse click event
				if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1)
				{
					pop_menu.show(textArea , e.getX(), e.getY());
				}        
			}               
		}
		);
		
		// textArea.setBackground(Color.red);
		// textArea.setEditable(false);
		
		//JScrollPane scrollpane = new JScrollPane();
		//scrollpane.add(textArea);
		
		JScrollPane scrollpane = new JScrollPane (textArea); //, 
		//JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
		
		tabbedPane.add("out",scrollpane);
		
		// --- table ---

		model = new XTableModel(xFilter);
		jTable = new JTable(model);
		
		jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // without this table would always resized to scrollPane size
		jTable.setColumnSelectionAllowed(true);
		jTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); // without this cell change is not updated
		
		/*jTable.getModel().addTableModelListener(new TableModelListener()
		{
      public void tableChanged(TableModelEvent e)
			{
				// System.out.println(e.getSource());	// object that fired event, useless, comes all from one object with a dynamic build name
				if (e.getType() == TableModelEvent.UPDATE)
				{
					System.out.println("update = "+e.getType()); // 0
				}
				
				if (e.getType() == TableModelEvent.INSERT)
				{
				  System.out.println("insert = "+e.getType()); // 1
				}
				
				if (e.getType() == TableModelEvent.DELETE)
				{
				  System.out.println("delete = "+e.getType()); // 2
				}
				
				if (e.getColumn()==TableModelEvent.ALL_COLUMNS)
				{
					 System.out.println("all columns = "+e.getColumn()); // -1
				}
				
				if (e.getFirstRow()==TableModelEvent.HEADER_ROW)
				{
					 System.out.println("header row = "+e.getFirstRow()); // i think it's -1 ?
				}
				System.out.println("columns = "+e.getColumn());
				System.out.println("first row = "+e.getFirstRow()+" |last row = "+e.getLastRow());
      }
		});*/

		JPopupMenu table_menu = new JPopupMenu("table");
		
		JMenuItem table_write = new JMenuItem("@edit cells");
		table_write.addActionListener(this);
		table_menu.add(table_write);
		
		jTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				//right mouse click event
				if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1)
				{
					// int row = jTable.rowAtPoint(e.getPoint());
					// int col = jTable.columnAtPoint(e.getPoint());
					// System.out.println("row "+ row + " | col" + col);
					// System.out.println(” Value in the cell clicked :”+ ” ” +table.getValueAt(row,col).toString());
					table_menu.show(jTable , e.getX(), e.getY());
				}        
			}               
		}
		);
		
		JScrollPane scrollPaneData = new JScrollPane (jTable);
		
		tabbedPane.add("data",scrollPaneData);
		
		//frame.add(scrollpane, BorderLayout.CENTER);
		
		frame.add(tabbedPane, BorderLayout.CENTER);

		//frame.setLayout(null);
		frame.setVisible(true);
		
		// problem on keylistener is, it only works to the focused object, so i need multiply listeners, todo find a better solution
		addKeyListener(new TastEvent());
		cmdField.addKeyListener(new TastEvent());
		textArea.addKeyListener(new TastEvent());
		jTable.addKeyListener(new TastEvent());
		jList.addKeyListener(new TastEvent());
		
		// --- list ---
		// some definitions are in class definitions, better do it here todo

		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jList.addListSelectionListener(this);
		
		JPopupMenu list_menu = new JPopupMenu("list");
		
		JMenuItem list_lock = new JMenuItem("@lock");
		list_lock.addActionListener(this);
		list_menu.add(list_lock);
		
		JMenuItem list_unlock = new JMenuItem("@unlock");
		list_unlock.addActionListener(this);
		list_menu.add(list_unlock);
		
		jList.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				//right mouse click event
				if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1)
				{
					list_menu.show(jList , e.getX(), e.getY());
				}        
			}               
		}
		);
		
		init(); // TODO this must be later independent thread from gui, so we wont block gui, when doing time expensive code
		working=false;
	}
	
	// ----------------------------------------------------
	// action
	// ----------------------------------------------------
	// currently all actions comes here, menu click and cmd input comes here
	public void actionPerformed(ActionEvent e)
  {
		actionX(e.getActionCommand());
  }
	// we split here for @commands and for search input
	public void actionX(String s)
	{
		if (working)
		{
			statusLabel.setText("please wait ...");
			return;
		}
		working=true;
		statusLabel.setText(s);
		if (s.startsWith("@"))
		{
			commandCmd(s.substring(1));
		}
		else
		{
			searchCmd(s);	
		}
		working=false;
	}
	
	// ----------------------------------------------------
	// commands
	// ----------------------------------------------------
	// first handle all commands, which don't change textarea logic binding to a filename (this is because of save logic)
	public void commandCmd(String s)
	{
		if (s.equals("info"))
		{
			JOptionPane.showMessageDialog(frame, VERSION, "Info", JOptionPane.PLAIN_MESSAGE);
			return;
    }
		
		if (s.equals("trim this"))
		{
			String stext="";
			int trimCounter=0;
			for (String sline : textArea.getText().split("\\n"))
			{
				String strim=sline.trim();
				if (!sline.equals(strim))
				{
					trimCounter++;
				}
				stext+=strim+"\n";
			}
			textArea.setText(stext);
			statusLabel.setText("trimed lines : "+trimCounter);
			return;
    }
		
		if (s.equals("unlock"))
		{
			// show all filter entrys, deselect all ??
			int size=xData.attributes.size();
			if (size>0)
			{
				listModel.clear();
				for (int i = 0; i < size; i++)
				{
					listModel.addElement(xData.attributes.get(i));
				}
			}
			else
			{
				// this should not happen, but to be save we launch an error here
				statusLabel.setText("error size 0 in @unlock");	
			}
			return;
    }
		
		if (s.equals("lock"))
		{
			// grap the selected filter options and use this for new list, deselect all
			if (jList.isSelectionEmpty())
			{
				statusLabel.setText("nothing selected");
				return;
			}
			// we grap all selected entrys and use this for next list
			ArrayList<String>	selectedHeaders=new ArrayList<String>();
			for (int i = jList.getMinSelectionIndex(); i <= jList.getMaxSelectionIndex(); i++)
			{
				if (jList.isSelectedIndex(i))
				{
					selectedHeaders.add(jList.getModel().getElementAt(i));
				}
			}
			
			int size=selectedHeaders.size();
			if (size>0)
			{
				listModel.clear();
				for (int i = 0; i < size; i++)
				{
					listModel.addElement(selectedHeaders.get(i));
				}
			}
			else
			{
				// this should not happen, but to be save we launch an error here
				statusLabel.setText("error size 0 in @lock");				
			}
			return;
    }
		
		if (s.equals("save"))
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(currentFilename));	
			int result = fileChooser.showSaveDialog(frame);
			
			if(result == JFileChooser.APPROVE_OPTION)
      {
				currentFilename=fileChooser.getSelectedFile().getPath();
				statusLabel.setText(currentFilename);
				
				if(writeFile(currentFilename,textArea.getText()))
				{
					statusLabel.setText(currentFilename+" saved!");
				}
				else
				{
					statusLabel.setText(currentFilename+" not saved!");
				}
			}
			return;
		}
		
		//no @save we kill currentFilename memory TODO we need better logic later
		currentFilename="";
		
		// check if command is a number
		// todo did we still need a browse logic by number index?, this was primary for debugging
		int number;
		try
		{
      number = Integer.parseInt(s);
    }
    catch (NumberFormatException ee)
		{
			number = -1;
		}
		
		// primitive logic to access objects by index
		if (number>=0)
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			statusLabel.setText("XObject:"+number+" (0-"+(xData.objectList.size()-1)+")\n");
			show(number);
			objectPointer=number;
			return;
		}
		
		if (s.equals("+"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			objectPointer++;
			statusLabel.setText("XObject:"+objectPointer+" (0-"+(xData.objectList.size()-1)+")\n");
			show(objectPointer);
			return;
		}
		
		if (s.equals("-"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			objectPointer--;
			statusLabel.setText("XObject:"+objectPointer+" (0-"+(xData.objectList.size()-1)+")\n");
			show(objectPointer);
			return;
		}
		
		if (s.equals("set path"))
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			// per default fileChooser starts in homedir, if we have a valid daimoninDir we take this
			if (Files.isDirectory(Paths.get(daimoninDir)))
			{
			  // File file =	new File(userDir); //thats not homeedir its appdir
				File file =	new File(daimoninDir); // todo clean this, work with path instead of string
			  fileChooser.setCurrentDirectory(file);
			}

			int result = fileChooser.showOpenDialog(frame);
			if(result == JFileChooser.APPROVE_OPTION)
      {
				// daimoninDir=fileChooser.getSelectedFile().getName();
				daimoninDir=fileChooser.getSelectedFile().getPath();
				// todo? test folder
				textArea.append("daimoninDir : "+daimoninDir+"\n");
				writeConfig("path "+daimoninDir); // TODO
				// currently we only write this path to config, and delete config
			}
			return;
		}
		
		if (s.equals("load"))
		{
			JFileChooser fileChooser = new JFileChooser();
			
			// is there marked text and is this a valid file? we use for for default selectedFile
			String selectedText=textArea.getSelectedText();
			if (selectedText!=null)
			{
				File file=new File(selectedText);
				if (file.exists())
				{
					fileChooser.setSelectedFile(file);
				}
			}
			int result = fileChooser.showOpenDialog(frame);
			if(result == JFileChooser.APPROVE_OPTION)
      {
				tabbedPane.setSelectedIndex(0);
				currentFilename=fileChooser.getSelectedFile().getPath();
				LoadFile(currentFilename);
			}
			return;
		}
		
		if (s.equals("load config"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			Path path = Paths.get(userDir, "archwizard.ini");
			currentFilename=path.toString();
			LoadFile(currentFilename);
			working=false;
			return;
		}
		
		if (s.equals("load proto"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			Path path = Paths.get(userDir, "proto.arc");
			currentFilename=path.toString();
			LoadFile(currentFilename);
			working=false;
			return;
		}
		
		if (s.equals("reset"))
		{
			tabbedPane.setSelectedIndex(0);
			init();
			working=false;
			return;
		}
		
		if (s.equals("search"))
		{
			// is there marked text and is this a valid file? we send it as search command
			// not really good, but it's a shortcut in search logic
			String selectedText=textArea.getSelectedText();
			cmdField.setText(selectedText);
			if (selectedText!=null)
			{
				searchCmd(selectedText);
			}
			return;
		}
		
		if (s.equals("editor"))
		{
			String externCommand=getValue(configList,"editor");
			if(externCommand==null)
			{
				statusLabel.setText("No extern editor defined! define this in archwizard.ini -> attribute -> editor . Needs reset.");
				return;
			}
			
			// is there marked text and is this a valid file? we send it as parameter behind command
			String selectedText=textArea.getSelectedText();
			if (selectedText!=null)
			{
				File file=new File(selectedText);
				if (file.exists())
				{
					externCommand+=" "+selectedText;
				}
				else
				{
					statusLabel.setText("selectedText is no valid file!");
					return;
				}
			}
						
			try
      {
          Runtime run  = Runtime.getRuntime();
					Process proc = run.exec(externCommand);
          // Process proc = run.exec("calc"); // calculator on win 10
      }
      catch (IOException ex)
      {
				statusLabel.setText("Error : extern editor definition : "+externCommand);
      }
			return;
		}
		
		if (s.equals("filter"))
		{
			if (filter)
			{
				frame.remove(filterscrollpane);
				filter=false;
				frame.revalidate();
				return;
			}
			
			frame.add(filterscrollpane, BorderLayout.WEST);
			filter=true;
			frame.revalidate();
			return;
		}
		
		if (s.equals("edit cells"))
		{
			// what we do if nothing is marked, we can use the mousepos?
			String input=JOptionPane.showInputDialog("Overwrite cells with");
			if (input==null) return;
			
			int[] columns=jTable.getSelectedColumns();
			int[] rows=jTable.getSelectedRows();
			
			for (int i=0;i<rows.length;i++)
			{
				for (int j=0;j<columns.length;j++)
				{
					jTable.setValueAt(input,rows[i],columns[j]);
				}
			}
		}
		
		if (s.equals("collect archetypes"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			startX(doXModeCollect);
			
			// we could improve this logic later, currently collect goes to textArea and we start a savedialog for user
			String filename = Paths.get(libDir, "archetypes").toString();
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(filename));	
			int result = fileChooser.showSaveDialog(frame);
			
			if(result == JFileChooser.APPROVE_OPTION)
      {
				currentFilename=fileChooser.getSelectedFile().getPath();
				
				if(writeFile(currentFilename,textArea.getText()))
				{
					statusLabel.setText(currentFilename+" saved!");
				}
				else
				{
					statusLabel.setText(currentFilename+" not saved!");
				}
			}
			return;
		}
				
		if (s.equals("write"))
		{
			writeX();
			return;
		}
		
		if (s.equals("show"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			showObjects();
			return;
		}
		
		if (s.equals("out"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText(sOut);
			return;
    }
		
		if (s.equals("log"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText(sLog);
			return;
    }
		
		if (s.equals("warning"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText(sWarning);
			return;
    }
		
		if (s.equals("error"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText(sError);
			return;
    }
		
		if (s.equals("configlist"))
		{
			tabbedPane.setSelectedIndex(0);
			showList(configList);
			return;
    }
		
		if (s.equals("protolist"))
		{
			tabbedPane.setSelectedIndex(0);
			showList(protoList);
			return;
    }
		
		if (s.equals("attributes"))
		{
			tabbedPane.setSelectedIndex(0);
			showList2(xData.attributes);
			return;
    }
		
		if (s.equals("pnglist"))
		{
			tabbedPane.setSelectedIndex(0);
			showList(pngList);
			return;
    }

		if (s.equals("objectpathlist"))
		{
			tabbedPane.setSelectedIndex(0);
			showList(objectPathList);
			return;
    }
		
		if (s.equals("animlist"))
		{
			tabbedPane.setSelectedIndex(0);
			showList(animList);
			return;			
    }
		
		if (s.equals("load animations"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			String filename = Paths.get(libDir, "animations").toString();
			
			LoadFile(filename);
			currentFilename=filename;
			return;
		}
		
		if (s.equals("load archetypes"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			String filename = Paths.get(libDir, "archetypes").toString();
			
			LoadFile(filename);
			currentFilename=filename;
			return;
		}
		
		if (s.equals("load artifacts"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			String filename = Paths.get(libDir, "artifacts").toString();
			
			LoadFile(filename);
			currentFilename=filename;
			return;
		}
		
		if (s.equals("load bmaps"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			String filename = Paths.get(libDir, "bmaps").toString();
			
			LoadFile(filename);
			currentFilename=filename;
			return;
		}
		
		if (s.equals("load help"))
		{
			tabbedPane.setSelectedIndex(0);
			textArea.setText("");
			String filename = userDir+"/"+HELP;
			
			LoadFile(filename);
			currentFilename=filename;
			return;
		}
		
		if (s.equals("backup"))
		{
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog(frame, "This will make a copy of all .arc files and a copy of artifact file.\nCopy has .bak extension. All .bak extensions with same name will be overwritten.", "Backup", dialogButton);
			
			if(dialogResult == 0)
			{
				startX(doXModeBackup);
			}
			return;
		}
		
		if (s.equals("restore"))
		{
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog(frame, "This will restore all .arc files, if there is a copy.\nCopy has .bak extension. All .arc extensions will be overwritten, if there is a copy.\nWARNING : This touch all files, so other programs like subversion notifies all files as changed!", "Restore", dialogButton);
			
			if(dialogResult == 0)
			{
				startX(doXModeRestore);
			}
			return;
		}
		
		if (s.equals("delete"))
		{
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog(frame, "This will delete copies of all .arc files.\nCopy has .bak extension. All .bak extensions where .arc files are found will be deleted.", "Delete", dialogButton);
			
			if(dialogResult == 0)
			{
				startX(doXModeDelete);
			}
			return;
		}
		
		if (s.equals("export"))
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(".csv")); // todo
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Comma-separated values", "csv")); //todo

			int result = fileChooser.showSaveDialog(frame);
			
			if(result == JFileChooser.APPROVE_OPTION)
      {
				String exportFilename=fileChooser.getSelectedFile().getPath();
				statusLabel.setText("export to "+exportFilename);
				export();

				if(writeFile(exportFilename,sexport))
				{
					statusLabel.setText(exportFilename+" saved!");
				}
				else
				{
					statusLabel.setText(exportFilename+" not saved!");
				}
			}
			return;
		}
		
		if (s.equals("import"))
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(".csv")); // todo
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Comma-separated values", "csv")); //todo

			int result = fileChooser.showOpenDialog(frame);
			
			if(result == JFileChooser.APPROVE_OPTION)
      {
				String importFilename=fileChooser.getSelectedFile().getPath();
				statusLabel.setText("import from "+importFilename);
				importCSV(importFilename);
				statusLabel.setText("finished ... ("+cError+") error(s) ("+cWarning+") warning(s)");
			}
			return;
		}
	}

	// ----------------------------------------------------
	// importCSV
	// ----------------------------------------------------
	// first line we take for headers, following lines are the objects
	// we currently use semicolon separated format
	// following lines can have less separators than header line, but not more
	// import ignores filters and import direct to xData
	void importCSV(String importFilename)
	{
		sError=""; cError=0;
		if (bLog) sLog="import "+importFilename+"\n";
		File fi = new File(importFilename);
		 
		try
		{
			String string = "";
			FileReader fr = new FileReader(fi);
			BufferedReader br = new BufferedReader(fr);
			
			// first line must be a valid header definition separated by semicolon
			String sline = br.readLine();
			String[] headerNames = sline.split(";"); // todo what happens if string is null? todo also allow other separators
			int length=headerNames.length;
			
			if (length>0)
			{
				if (bLog) sLog+=sline+"\n";
				// now read the rows
				while ((sline = br.readLine()) != null)
				{
					String[] rowCells = sline.split(";");
					// if (bLog) sLog+="rowCells :"+rowCells.length+"\n";
					// we allow smaller import data headers compared to our table header, todo we can remove this line?
					if (rowCells.length<=length)
					{
						// we need to id the data first, if it is an object or an artifact
						// this is same logic than in data class for arraylist, but here for arrays
						// todo optimize this, this is redundant
						String sID="";
						for (int i=0; i<rowCells.length;i++)
						{
							// if (bLog) sLog+="Headername "+i+"="+headerNames[i]+"\n";
							if (headerNames[i].equals("Object"))
							{
								// if (bLog) sLog+="found Object\n";
								// if we have a definition for Object this is from archetypes
								if (!rowCells[i].equals(""))
								{
									sID="Object "+rowCells[i];
									break;
								}
							}
							if (headerNames[i].equals("artifact"))
							{
								// if (bLog) sLog+="found artifact\n";
								// if we have a definition for artifact this is from artifacts
								if (!rowCells[i].equals(""))
								{
									sID="artifact "+rowCells[i];
									break;
								}
							}
						} //for
						// ---------------------------------------
						if (bLog) sLog+="identified data row as "+sID+"\n";
						if (sID.equals(""))
						{
							// we can't id this line
							sError+="can't identify row "+sline+"\n";
							cError++;
						}
						else
						{
							// now search for sID in XData
							int index=xData.find(sID);
							
							if (index<0)
							{
								sError+="can't find sID "+sID+" in xData\n";
								cError++;
							}
							else
							{
								// now copy data to xData
								if (bLog) sLog+="copy sID "+sID+" to index "+index+"\n";
								for (int i=0; i<rowCells.length; i++)
								{
									if (xData.set(index,headerNames[i],rowCells[i]))
									{
										if (bLog) sLog+="import "+headerNames[i]+" "+rowCells[i]+" ok.\n";
									}
									else
									{
										sError+="can't find sID "+sID+" in xData\n";
										cError++;	
									} // else
								} // for
							} // else
						}	// else
					} // else (rowCells.length==length)
					else
					{
						sError+="length of rowCells not same than length of columns "+sline+"\n";
						cError++;
					} // else				
				} // while
			} // if length>0
			else
			{
				sError+="error csv first row : 0 headers defined!\n";
				cError++;
			}
			return;
		}
		catch (Exception evt)
		{
			System.out.println("exception in importCSV\n"+evt+"\n");
			sError+="exception in importCSV\n";
			cError++;
			// log(""+evt+"\n");
			return;
		}
		 
 }
	
	// ----------------------------------------------------
	// export
	// ----------------------------------------------------	
	// export from table model
	// using filters todo check this <--------------------------------------
	
	void export()
	{
		int columnCount=model.getColumnCount();
		int rowCount=model.getRowCount();
		
		// build first line (columns)
		if (columnCount>0 && rowCount>0)
		{
			String exportNames="";
			for (int i=0; i < columnCount; i++)
			{
				exportNames+=model.getColumnName(i);
				if (i<columnCount-1)
				{
					exportNames+=";";
				}
			}
			sexport=exportNames+"\n";

			// build data (rows) 
			for (int i=0; i < rowCount; i++)
			{
				String exportValue="";
				for (int j = 0; j < columnCount; j++)
				{
					exportValue+=model.getValueAt(i,j);
					if (j<columnCount-1)
					{
						exportValue+=";";
					}
				} // for
				sexport+=exportValue+"\n";
			} // for
		} // if
	}
	
	// ----------------------------------------------------
	// search
	// ----------------------------------------------------
	
	// we change this now, instead of direct search output, we build an index list, from where we can build output later
	public void searchCmd(String s)
  {
		//we need to kill currentFilename memory TODO we need better logic later
		currentFilename="";
		
		xFilter.showObjectList.clear();
		xFilter.showAttributList.clear();
		xFilter.filterColumns=true;
		xFilter.filterRows=true;
		
		// archetypes have a named object definition, artifacts not
		// artifacts get there names from artifact, have empty object definition and a reference to an archetype object (def_arch)
		// Object is big O, artifact is small a
		
		// search for command in archetypes
		// searchmode 0 -> need exact string to search , example -> Object bread
		// searchmode 1 -> need equal start, example -> Object bre*
		int searchmode=0;
		if (s.endsWith("*"))
		{
			s=s.substring(0,s.length()-1);
			searchmode=1;
		}

		textArea.setText("");
		for (int i = 0; i < xData.objectList.size(); i++)
		{
			XObject xObject=xData.objectList.get(i); // reference
			ArrayList<Pair> object = xObject.attributeList; // reference
			boolean found=false;
			for (int j = 0; j < object.size(); j++)
			{
				Pair pair=object.get(j); // reference
				
				if (searchmode==0 && s.equals(pair.name+" "+pair.value) ||
						searchmode==1 && (pair.name+" "+pair.value).startsWith(s))
				{
					xFilter.showObjectList.add(i); // store index
					found=true;
					break; // if we found something, we don't need continue to search in this object
				}
			} // for
			
			if (found)
			{
				xFilter.addColumns(object); // ugly
			} // if (found)
		} // for
	
		buildListModel(); // filter choices
		showObjects(); // text output

		model.update();

	}
	
	// ----------------------------------------------------
	// functions
	// ----------------------------------------------------
	
	void buildFilterSelectedList()
	{
		ArrayList<String>	selectedHeaders=new ArrayList<String>();
		
		// filterSelectedList.clear();
		if (!jList.isSelectionEmpty())
		{
			//int minIndex = jList.getMinSelectionIndex();
			//int maxIndex = jList.getMaxSelectionIndex();
			for (int i = jList.getMinSelectionIndex(); i <= jList.getMaxSelectionIndex(); i++)
			{
				if (jList.isSelectedIndex(i))
				{
					selectedHeaders.add(jList.getModel().getElementAt(i));
					// System.out.println(jList.getModel().getElementAt(i));
				}
			}
		}
	}

	
	// todo, building the filter from the columns is not optimal
	// this can only be used after search command
	
	void buildListModel()
	{
		listModel.clear();
		int size=xFilter.getColumnCount();
		if (size>0)
		{
			for (int i = 0; i < size; i++)
			{
				listModel.addElement(xFilter.getColumnName(i));
			}
		}
	}
		
	void showObjects()
	{
		// todo clean this message, redundant logic
		if (xFilter.filterRows)
		{
			for (int i=0; i < xFilter.showObjectList.size(); i++)
			{
				int index=xFilter.showObjectList.get(i);
				XObject xObject=xData.objectList.get(index);
			
				ArrayList<Pair> object = xObject.attributeList;
				for (int j = 0; j < object.size(); j++)
				{
					Pair pair=object.get(j);
					objectPointer=index;
					show(objectPointer);	
					break; // if we found something and showed this object, we don't need to continue to search in object
				} // for
			} // for
		} // if Xfilter
		else
		{
			for (int i=0; i < xData.size(); i++)
			{
				XObject xObject=xData.objectList.get(i);
			
				ArrayList<Pair> object = xObject.attributeList;
				for (int j = 0; j < object.size(); j++)
				{
					Pair pair=object.get(j);
					objectPointer=i;
					show(objectPointer);	
					break; // if we found something and showed this object, we don't need to continue to search in object
				} // for
			} // for
		}
	}
	
	void showList2(ArrayList<String> list)
	{
		textArea.setText("");
		for (int i = 0; i < list.size(); i++)
		{
			String s=list.get(i);
			textArea.append(s+"\n");
		}		
	}
	
	void showList(ArrayList<Pair> list)
	{
		textArea.setText("");
		for (int i = 0; i < list.size(); i++)
		{
			Pair pair=list.get(i);
			textArea.append(pair.name+"->"+pair.value+"\n");
		}		
	}
	
	// ----------------------------------------------------
	// show
	// ----------------------------------------------------	
	// TODO we currently allow setting pointer in outside code higher than array and lower than 0, could be optimized
	void show(int index)
	{
		if (index>=0 && index<xData.objectList.size())
		{
			XObject xObject=xData.objectList.get(index);
			
			textArea.append(xObject.file+"\n");
			ArrayList<Pair> object = xObject.attributeList;
			for (int i = 0; i < object.size(); i++)
			{
				Pair pair=object.get(i);

				if (pair.value.equals(""))
				{
					textArea.append(pair.name+"\n");
				}
				else
				{
					textArea.append(pair.name+" "+pair.value+"\n");				
				}
			}	
		}
		return;
	}
	
	// todo remove this function
	void log(String s)
	{
		if(bLog) sLog+=s;
		sOut+=s;
		textArea.append(s);
	}
	
	
	// ----------------------------------------------------
	// init
	// ----------------------------------------------------		
	
	void init()
	{
		textArea.setText("");
		sOut="";
		sLog="";
		sWarning = "";
		sError = "";

		log(VERSION+"\n");
		log("userDir : "+userDir+"\n");
		
		objectPathList.clear();
		pngList.clear();
		animList.clear();
	
		counterArchetypes=0;
		counterArtifacts=0;
		
		xData.objectList.clear();
		objectPointer=0;
		
		xFilter.showObjectList.clear();
		xFilter.showAttributList.clear();
		xFilter.filterColumns=false;
		xFilter.filterRows=false;
		
		protoList.clear();
		configList.clear();
		
		filter=false;
		
		if(!readConfig()) return;
		log("configlist entries: "+configList.size()+"\n");
		daimoninDir=getValue(configList,"path");
		if(daimoninDir==null)
		{
			log("error : configlist missing definition for daimoninDir!\n");
			return;
		}
		log("daimoninDir : "+archDir+" ");
		
		if (Files.isDirectory(Paths.get(daimoninDir)))
		{
			log("ok.\n");
		}
		else
		{
			log("Error!\n");
			return;
		}
		
		readProto(); // TODO error
		log("protolist entries : "+protoList.size()+"\n");
		
		// todo check other pathes to ...
		archDir = Paths.get(daimoninDir, "arch").toString();
		log("archDir : "+archDir+"\n");
		
		statusLabel.setText("reading data, please wait ...");
		doXMode=doXModeReset;
		dirX(archDir);
		
		log("bmaps : "+pngList.size()+"\n");
		log("animations : "+animList.size()+"\n");
		log("arcfiles : "+objectPathList.size()+"\n");
		
		counterArchetypes=xData.objectList.size();
		log("arcobjects : "+counterArchetypes+"\n");
		
		libDir = Paths.get(daimoninDir, "server", "lib").toString();
		log("libDir : "+libDir+"\n");
		
		String filename = Paths.get(libDir, "artifacts").toString();
		readX(filename);
		counterArtifacts=xData.objectList.size()-counterArchetypes;
		log("artifacts : "+counterArtifacts+"\n");
		log("XObjects (archetypes+artifacts) : "+xData.objectList.size()+"\n");
		
		buildListModel(); // todo show this per default ???
		
		model.update();
		
		statusLabel.setText("ready.");
	}
	
	String getValue(ArrayList<Pair> list, String name)
	{
		for (int i = 0; i < list.size(); i++)
		{
			Pair pair=list.get(i);
			if (name.equals(pair.name))
			{
				return pair.value;
			}
		}
		return null;
	}
	
	
	// ----------------------------------------------------
	// readFile
	// ----------------------------------------------------	
	// overloaded readFile, return null means error
	String readFile(Path path)
	{
		return readFile(path.toString());
	}
	
	String readFile(String path)
	{
		log("readFile "+path+" ");
		File fi = new File(path);
    try
		{
			String string = "", sline = "";
      FileReader fr = new FileReader(fi);
      BufferedReader br = new BufferedReader(fr);
 
      while ((sline = br.readLine()) != null)
			{
				string += sline + "\n";
      }
			log("OK.\n");
			return string;
    }
    catch (Exception evt)
		{
			log(""+evt+"\n");
			return null;
    }
	}
	
	
	// ----------------------------------------------------
	// readProto
	// ----------------------------------------------------	
	// we ignore empty/comment lines, ignore all behind first word in line, ignore all between msg/endmsg and ignore all behind end
	// we make no double definition check on proto.arc
	boolean readProto()
	{
		Path path = Paths.get(userDir, PROTO);
		log("proto.arc : "+path+"\n");

		File fi = new File(path.toString());
    try
		{
			String sline = "";
      FileReader fr = new FileReader(fi);
      BufferedReader br = new BufferedReader(fr);
 
			boolean insideMsg=false;
 
      while ((sline = br.readLine()) != null)
			{
				sline=sline.trim(); // we also trim newfile
				if (sline.isEmpty())
				{
					continue; // ignore empty lines
				}
				
				if (sline.startsWith("#"))
				{
					continue; // ignore comments
				}
				
				Pair pair=new Pair();
				pair.name="";
				pair.value="";
				
				// todo we only search for " " ignoring tabs todo
				int index=sline.indexOf(" "); // -1 if there is no whitespace
				if (index<0)
				{
					pair.name=sline;
					pair.value="";
				}
				else
				{
					pair.name=sline.substring(0,index); // todo what happens when first char is whitespace
					pair.value=sline.substring(index+1);
				}
				
				// todo we need betterlogic? should we store all inside a message here on proto?
				if (insideMsg)
				{
					if (sline.startsWith("endmsg"))
					{
						protoList.add(pair);
						insideMsg=false;
					}
					continue;
				}
				
				if (sline.startsWith("msg"))
				{
					insideMsg=true;
					protoList.add(pair);
					continue;
				}
				
				protoList.add(pair);

				if (sline.startsWith("end"))
				{
					break;
				}
      } // while
			if (bLog) sLog+="proto:"+path+" loaded.";
			return true;
    }
    catch (Exception evt)
		{
			log(""+evt+"\n");
			return false;
    }
	}
	
	// ----------------------------------------------------
	// readConfig
	// ----------------------------------------------------	
	boolean readConfig()
	{
		Path path = Paths.get(userDir, CONFIG);
		log("configFile : "+path+"\n");
		
		File fi = new File(path.toString());
    try
		{
			String sline = "";
      FileReader fr = new FileReader(fi);
      BufferedReader br = new BufferedReader(fr);
  
      while ((sline = br.readLine()) != null)
			{
				sline=sline.trim();
				if (sline.isEmpty())
				{
					continue; // ignore empty lines
				}
				
				if (sline.startsWith("#"))
				{
					continue; // ignore comments
				}
				
				Pair pair=new Pair();
				pair.name="";
				pair.value="";
				
				// todo we only search for " " ignoring tabs todo
				int index=sline.indexOf(" "); // -1 if there is no whitespace
				if (index<0)
				{
					pair.name=sline;
					pair.value="";
				}
				else
				{
					pair.name=sline.substring(0,index); // todo what happens when first char is whitespace
					pair.value=sline.substring(index+1);
				}
				configList.add(pair);
      } // while
			if (bLog)	sLog+="config:"+path+" loaded.\n";
			return true;
    }
    catch (Exception evt)
		{
			log(""+evt+"\n");
			return false;
    }
	}
	// ----------------------------------------------------
	// writeConfig
	// ----------------------------------------------------
	void writeConfig(String string)
	{
		Path path = Paths.get(userDir, CONFIG);
		log("write configFile "+path+" ");
		
		boolean success = writeFile(path, string);
		
		if (success)
		{
			log("OK.\n");
		}
		else
		{
			log("Error!\n");	
		}
	}
		
	// ----------------------------------------------------
	// loadFile
	// ----------------------------------------------------	
	// todo don't need two loadFile
	boolean LoadFile(String filename)
	{
		File fi = new File(filename);
    try
		{
			String s1 = "", sl = "";
      FileReader fr = new FileReader(fi);
      BufferedReader br = new BufferedReader(fr);
      sl = br.readLine();
 
      while ((s1 = br.readLine()) != null)
			{
				sl = sl + "\n" + s1;
      }
				textArea.setText(sl);
				statusLabel.setText(filename);
    }
    catch (Exception evt)
		{
				statusLabel.setText(""+evt);
				return false;
    }
		return true;
	}
	
	String loadFile(String filename)
	{
		File fi = new File(filename);
		String string = "";
    try
		{
			String sLine = "";
      FileReader fr = new FileReader(fi);
      BufferedReader br = new BufferedReader(fr);
 
      while ((sLine = br.readLine()) != null)
			{
				string+=sLine+"\n";
      }
			if (bLog) sLog+="loaded "+filename+"\n";
    }
    catch (Exception evt)
		{
			sError+=""+evt+"\n";
			return ""; // or return null?
    }
		return string;
	}
	
	// ----------------------------------------------------
	// writeFile
	// ----------------------------------------------------	
	// overloaded
	// todo clean names
	boolean writeFile(Path path, String s)
	{
		return writeFile(path.toString(),s);
	}

	boolean writeFile(String filename, String s)
	{
		File fi = new File(filename);
    try
		{
      FileWriter fw = new FileWriter(fi);
      // BufferedWriter bw = new BufferedWriter(fw); // we don't need buffered writer here ...
      fw.write(s); // writer kills long strings without flush
			fw.flush();
			fw.close();
			
			sLog+="write "+filename;
			return true;
    }
    catch (Exception evt)
		{
			sLog+="error writing "+filename;
			// JOptionPane.showMessageDialog(frame, evt.getMessage());
			return false;
    }
	}
	// ----------------------------------------------------
	// copyFile
	// ----------------------------------------------------	
	void copyFile(String fromName, String toName)
	{
		Path fromPath = Paths.get(fromName);
    Path toPath = Paths.get(toName);
		try
		{
			Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
			if (bLog) sLog+="copy "+fromName+" to "+toName+"\n";
			// we want a verify mode ??? thats really slow then
			// assertThat(toPath).exists();
			// assertThat(Files.readAllLines(fromPath).equals(Files.readAllLines(toPath)));
		}
		catch (Exception evt)
		{
			if (bLog) sLog+="error : copy "+fromName+" to "+toName+"\n";
			sError+="error : copy "+fromName+" to "+toName+"\n";
			cError++;
		}
	}
	// ----------------------------------------------------
	// deleteFile
	// ----------------------------------------------------	
	void deleteFile(String name)
	{
		Path path = Paths.get(name);
		try
		{
			Files.deleteIfExists(path);
			if (bLog) sLog+="delete "+name+"\n";

		}
		catch (Exception evt)
		{
			if (bLog) sLog+="error : delete "+name+"\n";
			sError+="error : delete "+name+"\n";
			cError++;
		}
	}
	
	// ----------------------------------------------------
	// startX
	// ----------------------------------------------------
	// multifunction doing different things in subroutines depending on doXMode
	void startX(int _doXMode)
	{
		doXMode=_doXMode;
		cError=0;
		cWarning=0;
		sError="";
		sWarning="";
		sLog="";
		dirX(archDir);
		statusLabel.setText("finished ... ("+cError+") error(s) ("+cWarning+") warning(s)");
	}
	// ----------------------------------------------------
	// dirX
	// ----------------------------------------------------
	// this function browse all files recursive through a path
	void dirX(String path)
	{
		if (bLog) sLog+="dirX:"+path+"\n";
		try
		{
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles)
			{
				if (file.isFile())
				{
					doX(file);
				}
				else
				{
					dirX(path+"/"+file.getName());
				}	
			}
		}
		catch (Exception e)
		{
			if (bLog) sLog+="error DirX:"+path+"\n";
			sError+="error DirX:"+path+"\n"; // todo this is an error, we need give to output or error?
			//System.err.println(e.getMessage());
    }
	}
	
	// ----------------------------------------------------
	// doX
	// ----------------------------------------------------
	void doX(File file)
	{
		String fileName=file.getName();
		String filePath=file.getAbsolutePath();
		if (bLog) sLog+=filePath+"\n";
		
		if (doXMode==doXModeReset)
		{
			if (fileName.endsWith(".png"))
			{
				String name=fileName.substring(0,fileName.length()-4);
				Pair pair=new Pair();
				pair.name=name;
				pair.value=filePath;
				pngList.add(pair);
				if (bLog) sLog+="add:"+name+" to pngList\n";
				return;
			}
			
			if (fileName.endsWith(".anim"))
			{
				String name=fileName.substring(0,fileName.length()-5);
				sLog+="anim:"+name+"\n";
							
				Pair pair=new Pair();
				pair.name=name;
				pair.value=filePath;
				animList.add(pair);
				if (bLog) sLog+="add:"+name+" to animList\n";
				return;
			}
		}
		
		// read in .arc and data on reset
		// or doing backup, restore, delete, depending to .arc and .bak files
		if (fileName.endsWith(".arc"))
		{
			if (doXMode==doXModeReset)
			{
				readX(filePath); // here we have .arc file, we grab objects in readX.
				return;
			}

			if (doXMode==doXModeCollect)
			{
				// first append all files to textbox?
				textArea.append(loadFile(filePath));
				// start save dialog with default archetypname
			}
						
			if (doXMode==doXModeBackup)
			{
				copyFile(filePath,filePath+".bak");
			}
			else if(doXMode==doXModeRestore)
			{
				if (Files.exists(Paths.get(filePath+".bak")))
				{
					copyFile(filePath+".bak",filePath);
				}
				else
				{
					if (bLog) sLog+="no backup to restore : missing "+filePath+".bak\n";
					sWarning+="no backup to restore : missing "+filePath+".bak\n";
					cWarning++;
				}
			}
			else if(doXMode==doXModeDelete)
			{
				deleteFile(filePath+".bak");
			}
			return;
		}
					
		if (bLog) sLog+="ignore:"+fileName+"\n";
		return;
	}

	void readX(String filename)
	{
		if (bLog) sLog+="readX:"+filename+"\n";
		int errorCounter=0;
		 
		File fi = new File(filename);
    try
		{
			String sline = "", strim = "", stext = "", sobject = "";
      FileReader fr = new FileReader(fi);
      BufferedReader br = new BufferedReader(fr);
 
			ArrayList<Pair> object = null; // = new ArrayList<Pair>();

			boolean insideObject=false, insideMsg=false;
 
			int lineCounter=0;
      while ((sline = br.readLine()) != null)
			{
				lineCounter++;
				
				// later we flag this mode here, this slows down other logic
				strim=sline.trim();
				
				if (!strim.equals(sline))
				{
					errorCounter++;
					sError+=filename+" line "+lineCounter+" : untrimmed line\n";
				}
				sline=strim;
				
				if (sline.isEmpty()) continue; // ignore empty lines
				
				if (sline.startsWith("#")) continue; // to think are there commands in msg endmsgblock we want to keep?
			
				// archetypes Object definition starts with Object, Artifacts with Allowed, we use a free logic here -> first no comment defines object start
				if (!insideObject)
				{
					object = new ArrayList<Pair>();
					insideObject=true;
				}
				
				// lets ignore msgblock first TODO
				if (insideMsg)
				{
					if (sline.startsWith("endmsg"))
					{
						insideMsg=false;
					}
					continue;
				}
				
				if (sline.startsWith("msg"))
				{
					insideMsg=true;
					continue;
				}
		
				// here we can check if we have a valid attribute, todo later from proto.arcm, let's ignore this first
				
				Pair pair=new Pair();
				pair.name="";
				pair.value="";
				
				// we could use split, but this would produce a lot of array strings, we don't want to waste memory here
				int index=sline.indexOf(" "); // -1 if there is no whitespace
				if (index<0)
				{
					pair.name=sline;
					pair.value="";
				}
				else
				{
					pair.name=sline.substring(0,index); // todo what happens when first char is whitespace
					pair.value=sline.substring(index+1);
				}
				object.add(pair);
				
				if (sline.startsWith("end"))
				{
					XObject xObject = new XObject();
					xObject.name=xData.idXObject(object); // id the object and store the name
					xObject.file=filename;
					xObject.attributeList=object;
					xData.objectList.add(xObject); // store this data
					
					// add all new attributes to our attributes
					xData.addAttributes(object);

					insideObject=false;
					
					// we need to store the filename where Object comes from todo could be done better todo
					for (int i=0; i<object.size(); i++)
					{
						if (object.get(i).name.equals("Object"))
						{
							if (!object.get(i).value.equals(""))
							{
								Pair objectPath=new Pair();
								objectPath.name=object.get(i).value;
								objectPath.value=filename;
								objectPathList.add(objectPath);							
							}
						}
					} // for
				} // if
      } // while
    }
    catch (Exception evt)
		{
				if (bLog) sLog+="error:"+filename;
				sError+="error:"+filename;
				// JOptionPane.showMessageDialog(frame, evt.getMessage());
				return;
    }
		if (errorCounter>0)
		{
			log(errorCounter +" error in "+filename+"\n");
		}
	}
	
	void writeX()
	{
		if (xData.changed==false)
		{
			statusLabel.setText("nothing changed");
			return;
		}
		statusLabel.setText("writing data ...");

		sOut="";
		sLog="";
		sError="";
		sWarning="";
		
		for (int i=0; i<xData.objectList.size();i++)
		{
			XObject xObject = xData.objectList.get(i);
			if (xObject.changed)
			{
				fileToWrite(xObject);
				xObject.changed=false; // what we do on write errors with this flag ? todo
			} // if
		} // for
		statusLabel.setText("finished ... ("+cError+") error(s) ("+cWarning+") warning(s)");
	}
	
	// todo errors
	void fileToWrite(XObject xObject)
	{
		if (bLog) sLog+="->"+xObject.name+"\n";
		if (bLog) sLog+="->"+xObject.file+"\n";
		
		if (xObject.name=="")
		{
			sError+="fileToWrite Error : no xObject.name\n";
			System.out.println("fileToWrite Error : no xObject.name");
			return;			
		}
		
		if (xObject.file=="")
		{
			sError+="fileToWrite Error : no xObject.file\n";
			System.out.println("fileToWrite Error : no xObject.file");
			return;			
		}
		
		// now open file and parse objects till we found our object
		
		sOut+="updateX:"+xObject.file+"\n";
		if (bLog) sLog+="updateX:"+xObject.file+"\n";
		File fi = new File(xObject.file);
		String newfile="";
		
		try
		{
			boolean changed=false, found=false;
			String sline = "", stext = "", sobject = "", buffer="";
      FileReader fr = new FileReader(fi);
      BufferedReader br = new BufferedReader(fr);

			boolean insideObject=false, insideMsg=false;
			
			while ((sline = br.readLine()) != null)
			{
				sline=sline.trim(); // we also trim newfile, but trim is only saved, when we also change other things in file
				if (sline.isEmpty())
				{
					buffer+="\n";
					continue;
				}
				
				if (sline.startsWith("#"))
				{
					buffer+=sline+"\n";
					continue;
				}

				// archetypes Object definition starts with Object, Artifacts with Allowed, we use a free logic here
				if (!insideObject)
				{
					insideObject=true;
				}
				
				if (insideMsg)
				{
					if (sline.startsWith("endmsg"))
					{
						insideMsg=false;
					}
					buffer+=sline+"\n";
					continue;
				}
				
				if (sline.startsWith("msg"))
				{
					insideMsg=true;
					buffer+=sline+"\n";
					continue;
				}
				
				buffer+=sline+"\n";
				
				if (sline.equals(xObject.name))
				{
					found=true;
				}
		
				if (sline.startsWith("end"))
				{
					insideObject=false;
					
					if (found==true)
					{
						// now we need to do the same with the buffer
						// but this time its replace, delete or insert our object changes in buffer
						String temp=updateBuffer(buffer, xObject.attributeList);
						
						if (!(temp==null))
						{
							// ugly, should be improved
							if (!temp.equals(buffer))
							{
								buffer=temp;
								changed=true;
								if (bLog) sLog+="changed "+xObject.name+" in "+xObject.file+"\n";
							}
						}
						found=false;
					}
					
					newfile+=buffer;
					buffer="";
				}
			} // while
			// here we have build newfile
			// sOut+=newfile;
			if (changed) writeFile(xObject.file,newfile);
			xData.changed=false; // todo we ignoring write errors here on changelogic
		}
    catch (Exception evt)
		{
			if (bLog) sLog+="error:"+xObject.file;
			sError+="error:"+xObject.file;
			return;
    }
	}
	
	String updateBuffer(String buffer, ArrayList<Pair> _object)
	{
		// we need a copy of our object to work with, reference don't work here
		if (_object==null) return null;
		ArrayList<Pair> object = new ArrayList<Pair>(_object);

		try
		{
			boolean changed=false;
			String sline = "", stext = "", sobject = "", newBuffer = "";
      StringReader sr = new StringReader(buffer);
      BufferedReader br = new BufferedReader(sr);
			
			boolean insideMsg=false;
			
			while ((sline = br.readLine()) != null)
			{
				sline=sline.trim(); // we also trim newfile, but trim is only saved, when we also change other things in file
				if (sline.isEmpty())
				{
					newBuffer+="\n";
					continue;
				}
				
				if (sline.startsWith("#"))
				{
					newBuffer+=sline+"\n";
					continue;
				}

				if (insideMsg)
				{
					if (sline.startsWith("endmsg"))
					{
						insideMsg=false;
					}
					newBuffer+=sline+"\n";
					continue;
				}
				
				if (sline.startsWith("msg"))
				{
					insideMsg=true;
					newBuffer+=sline+"\n";
					continue;
				}
				
				if (sline.startsWith("end"))
				{
					// finding end, we need to write all new attribute definitions before end line
					for (int i=0; i<object.size();i++)
				  {
						Pair attribute=object.get(i);
						if (!attribute.name.equals("end"))
						{
							newBuffer+=attribute.name+" "+attribute.value+"\n";
							sOut+="added "+attribute.name+" "+attribute.value+"\n";
						  if (bLog) sLog+="added "+attribute.name+" "+attribute.value+"\n";						
						}
					}
					// and after this the end line
					newBuffer+=sline+"\n";
					// we ignore everything behind end now, normally this function is called with a clean end
					return newBuffer;
				}
				
				// here we need search equal XObject and replace or delete it
				boolean found=false;
				for (int i=0; i<object.size();i++)
				{
					Pair attribute=object.get(i);
					// todo  we can't use if startsWith, this is error. sp attribute is triggering speed_left line attribute
					// we use our own split logic, todo this should ne really improved later
					
					// we could use split, but this would produce a lot of array strings, we don't want to waste memory here
					int index=sline.indexOf(" "); // -1 if there is no whitespace
					String sName;
					String sCompare;
					if (index<0)
					{
						sName=sline;
						sCompare=sline+" "; // ugly workaround, todo optimize this logic
					}
					else
					{
						sName=sline.substring(0,index); // todo what happens when first char is whitespace
						sCompare=sline; // ugly workaround, todo optimize this logic
					}
					
					// if (sline.startsWith(attribute.name)) // dont work see above
					if (sName.equals(attribute.name))
					{
						if (sCompare.equals(attribute.name+" "+attribute.value))
						{
							// no change, line goes also with no change to newBuffer
							newBuffer+=sline+"\n";

						}
						else
						{
							// here we have a change, we build the line from our attribute definition
							newBuffer+=attribute.name+" "+attribute.value+"\n";
							sOut+="changed "+sline+" to "+attribute.name+" "+attribute.value+"\n";
							if (bLog) sLog+="changed "+sline+" to "+attribute.name+" "+attribute.value+"\n";
						}
						// now remove attribute from object, we don't need to compare this again
						object.remove(i);
						found=true;
						break;
					}
				} // for
				// when we have deleted xObject attribute in memory, we can't find this in file
				// so each not found line, will be deleted here
				if (!found)
				{
					sOut+="removed "+sline+"\n";
					if (bLog) sLog+="removed "+sline+"\n";
				}
			} // while
		}
		catch (Exception evt)
		{
			if (bLog) sLog+="updateBuffer read error";
			sError+="updateBuffer read error";
			cError++;
			return null;
    }
		// if we come here, we have a missing end, should not happen, but we better return a null error
		if (bLog) sLog+="updateBuffer error: no end attribute";
		sError+="updateBuffer error: no end attribute";
		cError++;
		object.clear(); // we clean this here. is it faster than to wait for garbage collector?
		return null;
	}
}
	
			//JList list = new JList(xAttributeList);
			//list.setVisibleRowCount(5);

			// JOptionPane.showMessageDialog(null, new JScrollPane(list));
			
			/*int bigNumber = 30001;
      String[] bigData = new String[bigNumber];
      for (int ii = 0; ii < bigNumber; ii++)
			{
				bigData[ii] = "String " + (ii + 1);
			}
			JList list = new JList(bigData);
			list.setVisibleRowCount(5);

			JOptionPane.showMessageDialog(null, new JScrollPane(list));*/
			
			
			// JOptionPane.showInputDialog("Dies ist ein Input Dialog");
			
			// JOptionPane.showConfirmDialog(null, "Dies ist ein Confirm Dialog");
			
			/*JOptionPane.showOptionDialog(null, "Dies ist ein Optionsdialog","Optionsdialog",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, 
                new String[]{"A", "B", "C"}, "B");*/
			
			/*String[] choices = { "A", "B", "C", "D", "E", "F" };
			String input = (String) JOptionPane.showInputDialog(null, "Choose now...",
        "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null, // Use
                                                                        // default
                                                                        // icon
        choices, // Array of choices
        choices[1]); // Initial choice
			System.out.println(input);*/
			
			// https://mkyong.com/java/how-to-get-the-filepath-of-a-file-in-java/
			
			// https://alvinalexander.com/java/joptionpane-showmessagedialog-examples-1/
			// JOptionPane.showMessageDialog(frame, "test");
			// JOptionPane.showMessageDialog(frame, "test", "Test Message", JOptionPane.INFORMATION_MESSAGE);
			// JOptionPane.showMessageDialog(frame, "test error", "Error Message", JOptionPane.ERROR_MESSAGE);
			// JOptionPane.showMessageDialog(frame, "test warning", "Warning Message", JOptionPane.WARNING_MESSAGE);
			// JOptionPane.showMessageDialog(frame, "test", "Test Message", JOptionPane.PLAIN_MESSAGE);
			
			
					// setting the number of rows or columns in DefaultListModel. If the new size is greater than the current size, new rows/columns are added to the end of the model, ff the new size is less than the current size, all rows/columns at index number and greater are discarded.
		// model.setRowCount(0);
		// model.setColumnCount(0);
		
		
			// https://docs.oracle.com/javase/7/docs/api/javax/swing/table/TableModel.html