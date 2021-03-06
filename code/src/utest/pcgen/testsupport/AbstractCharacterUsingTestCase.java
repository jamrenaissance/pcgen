/*
 * Copyright (c) 2010-2014 Tom Parker <thpr@users.sourceforge.net>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.testsupport;

import junit.framework.TestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.AutoLst;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.ability.VisibleToken;
import plugin.lsttokens.auto.LangToken;
import plugin.lsttokens.equipment.ProficiencyToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.primitive.language.LangBonusToken;

/*
 * Differs from code/src/test AbstractCharacterTestCase in that this does not
 * attempt to load all plugins (trying to be light weight)
 * 
 * In fact, use of this class can be seen as a weakness of the design of the
 * class/interface requiring the use of PlayerCharacter where the module being
 * tested in a utest environment should probably not be dependent on
 * PlayerCharacter in a fully isolated system
 */
public abstract class AbstractCharacterUsingTestCase extends TestCase {

	protected PCStat str;
	protected PCStat cha;
	protected PCStat dex;
	protected PCStat wis;
	protected PCStat intel;
	protected PCAlignment lg;
	protected PCAlignment ln;
	protected PCAlignment le;
	protected PCAlignment ng;
	protected PCAlignment tn;
	protected PCAlignment ne;
	protected PCAlignment cg;
	protected PCAlignment cn;
	protected PCAlignment ce;
	protected SizeAdjustment colossal;
	protected SizeAdjustment gargantuan;
	protected SizeAdjustment huge;
	protected SizeAdjustment large;
	protected SizeAdjustment medium;
	protected SizeAdjustment small;
	protected SizeAdjustment tiny;
	protected SizeAdjustment diminutive;
	protected SizeAdjustment fine;
	protected Language universal;
	protected Language other;

	private static final MultToken ABILITY_MULT_TOKEN = new plugin.lsttokens.ability.MultToken();
	private static final plugin.lsttokens.choose.LangToken CHOOSE_LANG_TOKEN = new plugin.lsttokens.choose.LangToken();
	private static final VisibleToken ABILITY_VISIBLE_TOKEN = new plugin.lsttokens.ability.VisibleToken();
	private static final ChooseLst CHOOSE_TOKEN = new plugin.lsttokens.ChooseLst();
	private static final AutoLst AUTO_TOKEN = new plugin.lsttokens.AutoLst();
	private static final LangToken AUTO_LANG_TOKEN = new plugin.lsttokens.auto.LangToken();
	private static final ProficiencyToken EQUIP_PROFICIENCY_TOKEN = new plugin.lsttokens.equipment.ProficiencyToken();
	private static final TypeLst EQUIP_TYPE_TOKEN = new plugin.lsttokens.TypeLst();
	private static final LangBonusToken LANGBONUS_PRIM = new plugin.primitive.language.LangBonusToken();
	private static final plugin.qualifier.language.PCToken PC_QUAL = new plugin.qualifier.language.PCToken();

	protected void finishLoad(LoadContext context)
	{
		context.getReferenceContext().buildDeferredObjects();
		context.getReferenceContext().buildDerivedObjects();
		context.resolveDeferredTokens();
		assertTrue(context.getReferenceContext().resolveReferences(null));
		context.resolvePostDeferredTokens();
		context.loadCampaignFacets();
	}

	protected void setUpPC() throws PersistenceLayerException
	{
		TokenRegistration.register(AUTO_LANG_TOKEN);
		TokenRegistration.register(ABILITY_VISIBLE_TOKEN);
		TokenRegistration.register(AUTO_TOKEN);
		TokenRegistration.register(CHOOSE_TOKEN);
		TokenRegistration.register(CHOOSE_LANG_TOKEN);
		TokenRegistration.register(ABILITY_MULT_TOKEN);
		TokenRegistration.register(EQUIP_TYPE_TOKEN);
		TokenRegistration.register(EQUIP_PROFICIENCY_TOKEN);
		TokenRegistration.register(LANGBONUS_PRIM);
		TokenRegistration.register(PC_QUAL);

		Globals.createEmptyRace();
		Globals.setUseGUI(false);
		Globals.emptyLists();
		GameMode gamemode = SettingsHandler.getGame();

		str = BuildUtilities.createStat("Strength", "STR");
		str.put(VariableKey.getConstant("LOADSCORE"), FormulaFactory
			.getFormulaFor("STRSCORE"));
		str.put(VariableKey.getConstant("OFFHANDLIGHTBONUS"), FormulaFactory
			.getFormulaFor(2));
		dex = BuildUtilities.createStat("Dexterity", "DEX");
		PCStat con = BuildUtilities.createStat("Constitution", "CON");
		intel = BuildUtilities.createStat("Intelligence", "INT");
		wis = BuildUtilities.createStat("Wisdom", "WIS");
		cha = BuildUtilities.createStat("Charisma", "CHA");

		AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
		lg = BuildUtilities.createAlignment("Lawful Good", "LG");
		ref.importObject(lg);
		ln = BuildUtilities.createAlignment("Lawful Neutral", "LN");
		ref.importObject(ln);
		le = BuildUtilities.createAlignment("Lawful Evil", "LE");
		ref.importObject(le);
		ng = BuildUtilities.createAlignment("Neutral Good", "NG");
		ref.importObject(ng);
		tn = BuildUtilities.createAlignment("True Neutral", "TN");
		ref.importObject(tn);
		ne = BuildUtilities.createAlignment("Neutral Evil", "NE");
		ref.importObject(ne);
		cg = BuildUtilities.createAlignment("Chaotic Good", "CG");
		ref.importObject(cg);
		cn = BuildUtilities.createAlignment("Chaotic Neutral", "CN");
		ref.importObject(cn);
		ce = BuildUtilities.createAlignment("Chaotic Evil", "CE");
		ref.importObject(ce);
		ref.importObject(BuildUtilities.createAlignment("None", "NONE"));
		ref.importObject(BuildUtilities.createAlignment("Deity's", "Deity"));

		gamemode.setBonusFeatLevels("3|3");

		SettingsHandler.setGame("3.5");

		ref.importObject(str);
		ref.importObject(dex);
		ref.importObject(con);
		ref.importObject(intel);
		ref.importObject(wis);
		ref.importObject(cha);

		fine = BuildUtilities.createSize("Fine");
		diminutive = BuildUtilities.createSize("Diminutive");
		tiny = BuildUtilities.createSize("Tiny");
		small = BuildUtilities.createSize("Small");
		medium = BuildUtilities.createSize("Medium");
		medium.put(ObjectKey.IS_DEFAULT_SIZE, true);
		large = BuildUtilities.createSize("Large");
		huge = BuildUtilities.createSize("Huge");
		gargantuan = BuildUtilities.createSize("Gargantuan");
		colossal = BuildUtilities.createSize("Colossal");

		universal = ref.constructCDOMObject(Language.class, "Universal");
		other = ref.constructCDOMObject(Language.class, "Other");
		SourceFileLoader.createLangBonusObject(Globals.getContext());
	}
}
