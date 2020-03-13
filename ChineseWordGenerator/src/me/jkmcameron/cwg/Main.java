package me.jkmcameron.cwg;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

public class Main
{
    private static class Word
    {
	public String name, pinyin, definition;
    }

    private static BufferedReader getFileAsReader(String filename)
    {
	InputStream stream = Main.class.getResourceAsStream(filename);

	return new BufferedReader(new InputStreamReader(stream));
    }

    private static ArrayList<Word> getWordListFromDatabase(BufferedReader reader, boolean getSimplified)
    {
	ArrayList<Word> words = new ArrayList<Word>();

	try
	{
	    String line;
	    while ((line = reader.readLine()) != null)
	    {
		StringBuilder sb = new StringBuilder(line);
		if (sb.charAt(sb.length() - 1) == '/')
		    sb.deleteCharAt(sb.length() - 1);
		line = sb.toString();

		Word word = new Word();

		String[] blocks = line.split("/", 2);
		{
		    String[] nameAndPinyin = blocks[0].split("\\[");

		    if (getSimplified)
			word.name = nameAndPinyin[0].split(" ")[1];
		    else
			word.name = nameAndPinyin[0].split(" ")[0];

		    word.pinyin = new StringBuilder(nameAndPinyin[1]).deleteCharAt(nameAndPinyin[1].length() - 2)
			    .toString();
		    word.definition = blocks[1];
		}

		words.add(word);
	    }
	}
	catch (IOException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return words;
    }

    private static ArrayList<Character> getCharacterListFromString(String source)
    {
	StringBuilder text = new StringBuilder(source);

	for (int i = 0; i < text.length(); i++)
	{
	    if (!Character.isIdeographic(text.charAt(i)))
		text.deleteCharAt(i);
	}

	String[] lines = text.toString().split("\n");

	ArrayList<Character> characters = new ArrayList<Character>();
	for (String line : lines)
	{
	    for (int i = 0; i < line.length(); i++)
	    {
		char character = line.charAt(i);

		if (!characters.contains(character))
		    characters.add(character);
	    }
	}

	return characters;
    }

    private static ArrayList<Word> findWordsFromCharacters(ArrayList<Character> characters, ArrayList<Word> dictionary)
    {
	ArrayList<Word> words = new ArrayList<Word>();

	for (Word word : dictionary)
	{
	    char[] name = word.name.toCharArray();

	    boolean containsUnknownCharacter = false;
	    boolean containsChineseCharacter = false;
	    for (char character : name)
	    {
		if (Character.isIdeographic(character))
		{
		    if (!characters.contains(character))
		    {
			containsUnknownCharacter = true;
		    }

		    containsChineseCharacter = true;
		}

	    }

	    if (containsChineseCharacter && !containsUnknownCharacter)
	    {
		words.add(word);
	    }
	}

	return words;
    }

    public static void main(String[] args)
    {
	BufferedReader database = getFileAsReader("cedict_ts.u8");
	ArrayList<Word> dictionary = getWordListFromDatabase(database, true);

	JFrame frame = new JFrame();
	frame.setBounds(100, 100, 450, 300);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	GridBagLayout gridBagLayout = new GridBagLayout();
	gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
	gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
	gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
	gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
	frame.getContentPane().setLayout(gridBagLayout);
	
	JToggleButton tglbtnNewToggleButton = new JToggleButton("Simplified");
	GridBagConstraints gbc_tglbtnNewToggleButton = new GridBagConstraints();
	gbc_tglbtnNewToggleButton.insets = new Insets(0, 0, 5, 5);
	gbc_tglbtnNewToggleButton.gridx = 0;
	gbc_tglbtnNewToggleButton.gridy = 0;
	frame.getContentPane().add(tglbtnNewToggleButton, gbc_tglbtnNewToggleButton);
	
	JButton btnNewButton = new JButton("Generate");
	
	GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
	gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
	gbc_btnNewButton.gridx = 1;
	gbc_btnNewButton.gridy = 0;
	frame.getContentPane().add(btnNewButton, gbc_btnNewButton);

	JScrollPane scrollPane_1 = new JScrollPane();
	GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
	gbc_scrollPane_1.gridwidth = 2;
	gbc_scrollPane_1.gridheight = 2;
	gbc_scrollPane_1.insets = new Insets(0, 0, 0, 5);
	gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
	gbc_scrollPane_1.gridx = 0;
	gbc_scrollPane_1.gridy = 1;
	frame.getContentPane().add(scrollPane_1, gbc_scrollPane_1);
	
	JTextArea textArea = new JTextArea();
	scrollPane_1.setViewportView(textArea);
	
	JScrollPane scrollPane = new JScrollPane();
	GridBagConstraints gbc_scrollPane = new GridBagConstraints();
	gbc_scrollPane.gridheight = 3;
	gbc_scrollPane.gridwidth = 6;
	gbc_scrollPane.fill = GridBagConstraints.BOTH;
	gbc_scrollPane.gridx = 2;
	gbc_scrollPane.gridy = 0;
	frame.getContentPane().add(scrollPane, gbc_scrollPane);
	
	JTextArea textArea_1 = new JTextArea();
	textArea_1.setEditable(false);
	scrollPane.setViewportView(textArea_1);
	
	btnNewButton.addActionListener(new ActionListener()
	{

	    @Override
	    public void actionPerformed(ActionEvent e)
	    {
		ArrayList<Character> characters = getCharacterListFromString(textArea.getText());
		ArrayList<Word> words = findWordsFromCharacters(characters, dictionary);

		StringBuilder text = new StringBuilder();

		for (Word word : words)
		{
		    text.append(word.name);
		    text.append(" (");
		    text.append(word.pinyin);
		    text.append("): ");
		    text.append(word.definition);
		    text.append("\n");
		}
		
		System.out.println(text.toString().split("\n").length);

		textArea_1.setText(text.toString());

	    }
	});
	
	frame.setVisible(true);
    }
}
