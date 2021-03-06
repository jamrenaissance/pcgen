/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
 * 
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
package tokenmodel.ability;

import pcgen.core.Ability;
import pcgen.rules.persistence.token.CDOMToken;
import tokenmodel.testsupport.AbstractAbilityGrantCheckTest;

public class AddVFeatAbilityTest extends AbstractAbilityGrantCheckTest
{
	private static final plugin.lsttokens.add.VFeatToken ADD_VFEAT_TOKEN =
			new plugin.lsttokens.add.VFeatToken();

	@Override
	protected CDOMToken<? super Ability> getGrantToken()
	{
		return ADD_VFEAT_TOKEN;
	}
}
