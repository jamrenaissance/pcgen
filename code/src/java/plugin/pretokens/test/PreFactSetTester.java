/*
 * Copyright 2014-15 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens.test;

import java.util.List;
import java.util.Map;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;
import pcgen.base.util.ObjectContainer;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.output.publish.OutputDB;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>PreFactTester</code> is responsible for testing FACT values on an object.
 */
public class PreFactSetTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter aPC, CDOMObject source) throws PrerequisiteException
	{
		String location = prereq.getCategoryName();
		String[] locationElements  = location.split("\\.");
		String test = prereq.getKey();
		String[] factinfo = test.split("=");
		String factid = factinfo[0];
		String factval = factinfo[1];

		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(LanguageBundle.getFormattedString(
				"PreFactSet.error", prereq.toString())); //$NON-NLS-1$
		}

		Map<String, Object> model = OutputDB.buildDataModel(aPC.getCharID());
		Object m = model.get(locationElements[0].toLowerCase());
		if (locationElements.length > 1)
		{
			TemplateHashModel thm = (TemplateHashModel) m;
			try
			{
				m = thm.get(locationElements[1]);
			}
			catch (TemplateModelException e)
			{
				throw new PrerequisiteException(e.getMessage());
			}
		}
		Iterable<CDOMObject> objModel = (Iterable<CDOMObject>) m;
		FactSetKey<?> fk = FactSetKey.valueOf(factid);
		
		int runningTotal =
				getRunningTotal(prereq, number, objModel, factval, fk);
		return countedTotal(prereq, runningTotal);
	}

	private <T> int getRunningTotal(final Prerequisite prereq, final int number,
		Iterable<CDOMObject> objModel, String factval, FactSetKey<T> fk)
	{
		T targetVal = fk.getFormatManager().convert(Globals.getContext(), factval);
		int runningTotal = 0;
		CDO: for (CDOMObject cdo : objModel)
		{
			List<ObjectContainer<T>> sets = cdo.getSetFor(fk);
			for (ObjectContainer<T> container:sets)
			{
				if (container.contains(targetVal))
				{
					runningTotal++;
					continue CDO;
				}
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return runningTotal;
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
    @Override
	public String kindHandled()
	{
		return "FACTSET"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		// Simplify the output when requiring a single source
		if (prereq.getOperator() == PrerequisiteOperator.GTEQ && ("1".equals(prereq.getOperand())))
		{
			return prereq.getKey();
		}

		final String foo = LanguageBundle.getFormattedString(
				"PreFactSet.toHtml", //$NON-NLS-1$
				new Object[] { prereq.getOperator().toDisplayString(),
						prereq.getOperand(), prereq.getKey() });
		return foo;
	}

}
