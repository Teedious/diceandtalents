// GameMaster Dice
// Copyright (C) 2014 David Pflug
// Copyright (C) 2011-2014 Georg Lukas
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

package no.domain.diceandtalents.DiceStuff;

import android.content.Context;

import java.util.Random;

public class DSADiceSet extends DiceSet {
	public static final int DSA_DICE_COUNT = 3;
	public static final int DSA_DICE_SIDES = 20;

	public DSADiceSet() {
		count = DSA_DICE_COUNT;
		sides = DSA_DICE_SIDES;
		modifier = 0;
	}

	public DSADiceSet(int modifier){
		this();
		this.modifier=modifier;
	}

	public String roll(Context ctx, Random gen) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			int roll1 = gen.nextInt(sides) + 1;
			sb.append(roll1);
			if (i < count-1)
				sb.append(" · ");
		}
		return sb.toString();
	}

	public String toString() {
		return DSA;
	}

	public int hashCode() {
		return 1032000;
	}
}
