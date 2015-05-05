package com.zhongshu.sged;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.LookAndFeelDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.gui.laf.InfoNodeLookAndFeel;

public class Editor {
	public static final int PALETTE_COMMON = 0;
	public static final int PALETTE_DIAGRAM = 1;
	public static final int PALETTE_SIZE = 2;

	private RootWindow rootWindow;
	private View[] paletteViews = new View[PALETTE_SIZE];
	private View jungView = null;
	private JungPanel jungPanel = null;
	private View propertyView = null;
	private ViewMap viewMap = new ViewMap();
	private DockingWindowsTheme currentTheme = new LookAndFeelDockingTheme();
	private RootWindowProperties properties = new RootWindowProperties();
	private JFrame frame = new JFrame("Swing graph editor demo");

	public Editor() {
		createRootWindow();
		setDefaultLayout();
		showFrame();
	}

	private static JComponent createViewComponent(String text) {
		StringBuffer sb = new StringBuffer();

		for (int j = 0; j < 100; j++)
			sb.append(text + ". This is line " + j + "\n");

		return new JScrollPane(new JTextArea(sb.toString()));
	}

	private void createRootWindow() {
		// Create the views
		for (int i = 0; i < PALETTE_SIZE; i++) {
			paletteViews[i] = new View("View " + i, null, createViewComponent("View " + i));
			viewMap.addView(i, paletteViews[i]);
		}

		jungPanel = new JungPanel();
		jungView = new View("Jung", null, jungPanel);
		propertyView = new View("Properties", null, createViewComponent("Property"));

		rootWindow = DockingUtil.createRootWindow(viewMap, null, true);
		properties.addSuperObject(currentTheme.getRootWindowProperties());
		rootWindow.getRootWindowProperties().addSuperObject(properties);
		//rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
	}

	private void setDefaultLayout() {
		TabWindow tabPallets = new TabWindow(paletteViews);
		rootWindow.setWindow(new SplitWindow(true, 0.3f, 
				             new SplitWindow(false, 0.7f, tabPallets, propertyView), jungView));
	}

	private void showFrame() {
		frame.getContentPane().add(createToolBar(), BorderLayout.NORTH);
		frame.getContentPane().add(rootWindow, BorderLayout.CENTER);
		frame.setJMenuBar(createMenuBar());
		frame.setSize(900, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();

		JButton btnEditMode = new JButton("");
		btnEditMode.setIcon(new ImageIcon(Editor.class.getResource("/net/infonode/docking/theme/internal/resource/xp/button_maximize_normal.png")));
		btnEditMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jungPanel.setEditMode();
			}
		});
		toolBar.add(btnEditMode);
		
		JButton btnPickMode = new JButton("");
		btnPickMode.setIcon(new ImageIcon(Editor.class.getResource("/net/infonode/tabbedpanel/theme/internal/resource/xp/button_dropdown_normal.png")));
		btnPickMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jungPanel.setPickMode();
			}
		});
		toolBar.add(btnPickMode);		
		
		

		return toolBar;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menu = new JMenuBar();
		menu.add(createFileMenu());
		menu.add(createViewMenu());
		return menu;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.add("New").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		fileMenu.add("Open").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jungPanel.loadGraph("1.graphml");
			}
		});

		fileMenu.add("Save").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jungPanel.saveGraph("1.graphml");
			}
		});

		return fileMenu;
	}

	private JMenu createViewMenu() {
		JMenu buttonsMenu = new JMenu("View");

		buttonsMenu.add("Enable Close").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				properties.getDockingWindowProperties().setCloseEnabled(true);
			}
		});

		buttonsMenu.add("Hide Close Buttons").addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						properties.getDockingWindowProperties()
								.setCloseEnabled(false);
					}
				});

		buttonsMenu.add("Freeze Layout").addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						freezeLayout(true);
					}
				});

		buttonsMenu.add("Unfreeze Layout").addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						freezeLayout(false);
					}
				});
		
		buttonsMenu.add("Reset Layout").addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setDefaultLayout();
					}
				});
		
		return buttonsMenu;
	}

	private void freezeLayout(boolean freeze) {
		// Freeze window operations
		properties.getDockingWindowProperties().setDragEnabled(!freeze);
		properties.getDockingWindowProperties().setCloseEnabled(!freeze);
		properties.getDockingWindowProperties().setMinimizeEnabled(!freeze);
		properties.getDockingWindowProperties().setRestoreEnabled(!freeze);
		properties.getDockingWindowProperties().setMaximizeEnabled(!freeze);
		properties.getDockingWindowProperties().setUndockEnabled(!freeze);
		properties.getDockingWindowProperties().setDockEnabled(!freeze);

		// Freeze tab reordering inside tabbed panel
		properties.getTabWindowProperties().getTabbedPanelProperties()
				.setTabReorderEnabled(!freeze);
	}

	public static void main(String[] args) throws Exception {
		// Set InfoNode Look and Feel
		UIManager.setLookAndFeel(new InfoNodeLookAndFeel());

		// Docking windwos should be run in the Swing thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {
				}
				new Editor();
			}
		});
	}
}
