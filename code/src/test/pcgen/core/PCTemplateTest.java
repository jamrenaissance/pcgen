/*
 * PCTemplateTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Jan 3, 2007
 *
 * $Id: PCTemplateTest.java 1855 2007-01-02 06:42:02Z jdempsey $
 *
 */
package pcgen.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.PCGenTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.list.AbilityList;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;
import pcgen.rules.context.LoadContext;

/**
 * <code>PCTemplateTest</code> tests the fucntion of the PCTemplate class.
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class PCTemplateTest extends AbstractCharacterTestCase
{
	private PCClass testClass;
	private GenericLoader<PCTemplate> loader = new GenericLoader<PCTemplate>(PCTemplate.class);
	
	/**
	 * Constructs a new <code>PCTemplateTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public PCTemplateTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new <code>PCTemplateTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public PCTemplateTest(final String name)
	{
		super(name);
	}

	/**
	 * Run the tests
	 * @param args
	 */
	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(PCTemplateTest.class);
	}

	/**
	 * Returns all the test methasVFeatshods in this class.
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		// quick method, adds all methods beginning with "test"
		return new TestSuite(PCTemplateTest.class);
	}

	/**
	 * Test the definition and application of abilities. 
	 * @throws PersistenceLayerException 
	 * @throws MalformedURLException 
	 */
	public void testAddAbility() throws PersistenceLayerException, MalformedURLException
	{
		LoadContext context = Globals.getContext();
		// Create some abilities to be added
		AbilityCategory cat = context.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "TestCat");
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCDOMCategory(cat);
		context.getReferenceContext().importObject(ab1);
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCDOMCategory(cat);
		context.getReferenceContext().importObject(ab2);

		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		loader
			.parseLine(
				context,
				null,
				"Template1	ABILITY:TestCat|AUTOMATIC|Ability1	ABILITY:TestCat|AUTOMATIC|Ability2", source);
		PCTemplate template = context.getReferenceContext().silentlyGetConstructedCDOMObject(PCTemplate.class, "Template1");
		context.getReferenceContext().importObject(ab1);
		context.getReferenceContext().importObject(ab2);
		assertTrue(context.getReferenceContext().resolveReferences(null));
		CDOMReference<AbilityList> autoList = AbilityList.getAbilityListReference(cat, Nature.AUTOMATIC);
		Collection<CDOMReference<Ability>> listMods = template.getListMods(autoList);
		assertEquals(2, listMods.size());
		Iterator<CDOMReference<Ability>> iterator = listMods.iterator();
		CDOMReference<Ability> ref1 = iterator.next();
		CDOMReference<Ability> ref2 = iterator.next();
		Collection<Ability> contained1 = ref1.getContainedObjects();
		Collection<Ability> contained2 = ref2.getContainedObjects();
		assertEquals(1, contained1.size());
		assertEquals(1, contained2.size());
		assertTrue(contained1.contains(ab1) || contained2.contains(ab1));
		assertTrue(contained1.contains(ab2) || contained2.contains(ab2));

		// Add the template to the character
		PlayerCharacter pc = getCharacter();
		pc.addTemplate(template);
		assertTrue("Character should have ability1.", hasAbility(pc, cat,
			Nature.AUTOMATIC, ab1));
		assertTrue("Character should have ability2.", hasAbility(pc, cat,
			Nature.AUTOMATIC, ab2));
	}

	/**
	 * Test the definition and application of abilities of category FEAT. 
	 * @throws PersistenceLayerException 
	 * @throws MalformedURLException 
	 */
	public void testAddFeatAbility() throws PersistenceLayerException, MalformedURLException
	{
		LoadContext context = Globals.getContext();
		// Create some abilities to be added
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCDOMCategory(AbilityCategory.FEAT);
		context.getReferenceContext().importObject(ab1);
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCDOMCategory(AbilityCategory.FEAT);
		context.getReferenceContext().importObject(ab2);

		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		loader
			.parseLine(
					context,
					null,
				"Template1	ABILITY:FEAT|AUTOMATIC|Ability1	ABILITY:FEAT|AUTOMATIC|Ability2", source);
		PCTemplate template = context.getReferenceContext().silentlyGetConstructedCDOMObject(PCTemplate.class, "Template1");
		context.getReferenceContext().importObject(ab1);
		context.getReferenceContext().importObject(ab2);
		assertTrue(context.getReferenceContext().resolveReferences(null));
		CDOMReference<AbilityList> autoList = AbilityList.getAbilityListReference(AbilityCategory.FEAT, Nature.AUTOMATIC);
		Collection<CDOMReference<Ability>> listMods = template.getListMods(autoList);
		assertEquals(2, listMods.size());
		Iterator<CDOMReference<Ability>> iterator = listMods.iterator();
		CDOMReference<Ability> ref1 = iterator.next();
		CDOMReference<Ability> ref2 = iterator.next();
		Collection<Ability> contained1 = ref1.getContainedObjects();
		Collection<Ability> contained2 = ref2.getContainedObjects();
		assertEquals(1, contained1.size());
		assertEquals(1, contained2.size());
		assertTrue(contained1.contains(ab1) || contained2.contains(ab1));
		assertTrue(contained1.contains(ab2) || contained2.contains(ab2));

		// Add the template to the character
		PlayerCharacter pc = getCharacter();
		pc.addTemplate(template);
		// Need to do this to populate the ability list
		//pc.getAutomaticAbilityList(AbilityCategory.FEAT);
		assertTrue("Character should have ability1.", hasAbility(pc, AbilityCategory.FEAT,
			Nature.AUTOMATIC, ab1));
		assertTrue("Character should have ability2.", hasAbility(pc, AbilityCategory.FEAT,
			Nature.AUTOMATIC, ab2));
	}

	/**
	 * Test the definition and application of abilities. 
	 * @throws PersistenceLayerException 
	 * @throws MalformedURLException 
	 */
	public void testAddLevelAbility() throws PersistenceLayerException, MalformedURLException
	{
		LoadContext context = Globals.getContext();
		
		AbilityCategory cat = context.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "TestCat");
		// Create some abilities to be added
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCDOMCategory(cat);
		context.getReferenceContext().importObject(ab1);
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCDOMCategory(cat);
		context.getReferenceContext().importObject(ab2);

		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		loader
			.parseLine(
					context,
					null,
				"Template1	LEVEL:2:ABILITY:TestCat|AUTOMATIC|Ability1	ABILITY:TestCat|AUTOMATIC|Ability2", source);
		PCTemplate template = context.getReferenceContext().silentlyGetConstructedCDOMObject(PCTemplate.class, "Template1");
		context.getReferenceContext().importObject(ab1);
		context.getReferenceContext().importObject(ab2);
		assertTrue(context.getReferenceContext().resolveReferences(null));
		CDOMReference<AbilityList> autoList = AbilityList.getAbilityListReference(cat, Nature.AUTOMATIC);
		Collection<CDOMReference<Ability>> listMods = template.getListMods(autoList);
		assertEquals(1, listMods.size());
		Iterator<CDOMReference<Ability>> iterator = listMods.iterator();
		CDOMReference<Ability> ref1 = iterator.next();
		Collection<Ability> contained1 = ref1.getContainedObjects();
		assertEquals(1, contained1.size());
		assertTrue(contained1.contains(ab2));

		List<PCTemplate> lvlTemplates = template.getSafeListFor(ListKey.LEVEL_TEMPLATES);
		assertEquals(1, lvlTemplates.size());
		PCTemplate lvl2 = lvlTemplates.get(0);
		assertEquals(2, lvl2.get(IntegerKey.LEVEL).intValue());
		
		listMods = lvl2.getListMods(autoList);
		assertEquals(1, listMods.size());
		iterator = listMods.iterator();
		ref1 = iterator.next();
		contained1 = ref1.getContainedObjects();
		assertEquals(1, contained1.size());
		assertTrue(contained1.contains(ab1));

		// Add the template to the character
		PlayerCharacter pc = getCharacter();
		pc.addTemplate(template);
		assertFalse("Character should not have ability1.", hasAbility(pc, cat,
			Nature.AUTOMATIC, ab1));
		assertTrue("Character should have ability2.", hasAbility(pc, cat,
			Nature.AUTOMATIC, ab2));
		
		// Level the character up, testing for when the level tag kicks in
		pc.incrementClassLevel(1, testClass);
		pc.calcActiveBonuses();
		assertFalse("Character should not have ability1.", hasAbility(pc, cat,
			Nature.AUTOMATIC, ab1));

		pc.incrementClassLevel(1, testClass);
		pc.calcActiveBonuses();
		assertTrue("Character should have ability1.", hasAbility(pc, cat,
			Nature.AUTOMATIC, ab1));
		
	}

	/**
	 * Test the definition and application of abilities of category FEAT. 
	 * @throws PersistenceLayerException 
	 * @throws MalformedURLException 
	 */
	public void testAddLevelFeatAbility() throws PersistenceLayerException, MalformedURLException
	{
		// Create some abilities to be added
		LoadContext context = Globals.getContext();
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCDOMCategory(AbilityCategory.FEAT);
		context.getReferenceContext().importObject(ab1);
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCDOMCategory(AbilityCategory.FEAT);
		context.getReferenceContext().importObject(ab2);

		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		loader
			.parseLine(
					context,
				null,
				"Template1	LEVEL:2:ABILITY:Feat|AUTOMATIC|Ability1	ABILITY:Feat|AUTOMATIC|Ability2", source);
		PCTemplate template = context.getReferenceContext().silentlyGetConstructedCDOMObject(PCTemplate.class, "Template1");
		context.getReferenceContext().importObject(ab1);
		context.getReferenceContext().importObject(ab2);
		assertTrue(context.getReferenceContext().resolveReferences(null));
		CDOMReference<AbilityList> autoList = AbilityList.getAbilityListReference(AbilityCategory.FEAT, Nature.AUTOMATIC);
		Collection<CDOMReference<Ability>> listMods = template.getListMods(autoList);
		assertEquals(1, listMods.size());
		Iterator<CDOMReference<Ability>> iterator = listMods.iterator();
		CDOMReference<Ability> ref1 = iterator.next();
		Collection<Ability> contained1 = ref1.getContainedObjects();
		assertEquals(1, contained1.size());
		assertTrue(contained1.contains(ab2));

		List<PCTemplate> lvlTemplates = template.getSafeListFor(ListKey.LEVEL_TEMPLATES);
		assertEquals(1, lvlTemplates.size());
		PCTemplate lvl2 = lvlTemplates.get(0);
		assertEquals(2, lvl2.get(IntegerKey.LEVEL).intValue());
		
		listMods = lvl2.getListMods(autoList);
		assertEquals(1, listMods.size());
		iterator = listMods.iterator();
		ref1 = iterator.next();
		contained1 = ref1.getContainedObjects();
		assertEquals(1, contained1.size());
		assertTrue(contained1.contains(ab1));

		// Add the template to the character
		PlayerCharacter pc = getCharacter();
		pc.addTemplate(template);
		assertFalse("Character should not have ability1.", hasAbility(pc, AbilityCategory.FEAT,
			Nature.AUTOMATIC, ab1));
		assertTrue("Character should have ability2.", hasAbility(pc, AbilityCategory.FEAT,
			Nature.AUTOMATIC, ab2));
		
		// Level the character up, testing for when the level tag kicks in
		pc.incrementClassLevel(1, testClass);
		pc.calcActiveBonuses();
		assertFalse("Character should not have ability1.", hasAbility(pc, AbilityCategory.FEAT,
			Nature.AUTOMATIC, ab1));

		pc.incrementClassLevel(1, testClass);
		pc.calcActiveBonuses();
		assertTrue("Character should have ability1.", hasAbility(pc, AbilityCategory.FEAT,
			Nature.AUTOMATIC, ab1));
		
	}
	
	/**
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		// Create the test class
		testClass = new PCClass();
		testClass.setName("TestClass");
		testClass.put(StringKey.KEY_NAME, "KEY_TestClass");
		Globals.getContext().getReferenceContext().importObject(testClass);

	}

	/**
	 * @see pcgen.AbstractCharacterTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
}
