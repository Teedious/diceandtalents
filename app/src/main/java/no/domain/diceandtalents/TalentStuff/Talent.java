package no.domain.diceandtalents.TalentStuff;

import java.io.Serializable;
import java.util.Random;

import no.domain.diceandtalents.DiceStuff.DSADiceSet;

public class Talent implements Serializable
{
	public static final int MIN_VAL = 0;
	public static final int MAX_VAL = 30;
	public static final int REDUCE_FROM_TALENT = 0;
	public static final int REDUCE_FROM_ATTRIBUTE = 1;

	private String name;
	private int[] attrID;
	private static int defaultRollStyle = REDUCE_FROM_TALENT;
	private int rollStyle;
	private int value=0;
	private int[] lastDiceRoll;
	private int lastTalentRollResult;
	private int lastMod;

	public int[] getLastDiceRoll()
	{
		return lastDiceRoll;
	}

	public int getLastTalentRollResult()
	{
		return lastTalentRollResult;
	}

	public int getLastMod()
	{
		return lastMod;
	}

	public Talent(String name, int[] attrID, int value, int rollStyle){
		this.name = name;
		this.attrID=attrID;
		setValue(value);
		setRollStyle(rollStyle);
	}
	public Talent(String name,int attrID1, int attrID2, int attrID3, int value, int rollStyle){
		this(name,new int[]{attrID1, attrID2, attrID3},value, rollStyle);
	}

	public Talent(String name,int attrID1, int attrID2, int attrID3, int value){
		this(name, attrID1, attrID2, attrID3,  value, defaultRollStyle);
	}

	public Talent(String name,int[] attrID, int value){
		this(name, attrID,value, defaultRollStyle);
	}

	boolean setRollStyle(int rollStyle){
		if(rollStyle != REDUCE_FROM_ATTRIBUTE &&
				rollStyle != REDUCE_FROM_TALENT)
		{
			this.rollStyle = REDUCE_FROM_TALENT;
			return false;
		}
		else
		{
			this.rollStyle = rollStyle;
			return true;
		}
	}

	public static boolean setDefaultRollStyle(int dRStyle){
		if(dRStyle != REDUCE_FROM_ATTRIBUTE &&
				dRStyle != REDUCE_FROM_TALENT)
		{
			defaultRollStyle = REDUCE_FROM_TALENT;
			return false;
		}
		else
		{
			defaultRollStyle = dRStyle;
			return true;
		}
	}

	public int[] getAttrID(){
		return attrID;
	}

	public int[] getAttr() {
		int[] attr = new int[attrID.length];
		for(int i=0;i<attr.length;i++){
			attr[i]=Attributes.getValues()[attrID[i]];
		}
		return attr;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setAttrID(int[] attrID)
	{
		this.attrID = attrID;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		if(value >=MIN_VAL && value<=MAX_VAL) this.value = value;
	}

	public static int getDefaultRollStyle() {
		return defaultRollStyle;
	}

	public int getRollStyle() {
		return rollStyle;
	}



	public int roll(Random rng, int modifier){
		int[] diceResults = new DSADiceSet(modifier).roll(rng);
		int result = computeResult(diceResults, modifier);
		storeLastRoll(diceResults, modifier, result);
		return result;
	}

	private void storeLastRoll(int[] diceResults, int mod, int result){
		lastDiceRoll = diceResults;
		lastMod = mod;
		lastTalentRollResult = result;
	}

	public int computeResult(int[] diceResults, int modifier){
		int[] modAttr = getAttr();
		int tempVal = value;
		if(rollStyle == REDUCE_FROM_TALENT){
			if(tempVal >= modifier){
				tempVal -=modifier;
				modifier=0;
			}else{
				modifier -=tempVal;
				tempVal = 0;
			}
		}
		int r = modifier%3;
		if(r==2)modAttr[1]-=1;
		if(r>=1)modAttr[0]-=1;

		modifier/=3;
		for(int i=0;i<3;i++){
			modAttr[i]-=modifier;
			tempVal -= modAttr[i]>=diceResults[i] ? 0 : diceResults[i]-modAttr[i];
		}
		return tempVal<value?tempVal:value;
	}
/*
	public String stringResult(int[] diceResults, int modifier){
		int[] modAttr = attr.clone();
		int mod = modifier;
		int tempVal = value;
		if(rollStyle == REDUCE_FROM_TALENT){
			if(tempVal >= mod){
				tempVal -=mod;
				mod=0;
			}else{
				mod -=tempVal;
				tempVal = 0;
			}
		}
		int r = mod%3;
		if(r==2)modAttr[1]-=1;
		if(r>=1)modAttr[0]-=1;

		mod/=3;
		for(int i=0;i<3;i++){
			modAttr[i]-=mod;
			tempVal -= modAttr[i]>=diceResults[i] ? 0 : diceResults[i]-modAttr[i];
		}
		tempVal =  tempVal<value?tempVal:value;
		return
				", W: "+(diceResults[0]<10?" ":"")+diceResults[0]+
				":"+(diceResults[1]<10?" ":"")+diceResults[1]+
				":"+(diceResults[2]<10?" ":"")+diceResults[2]+
				", "+(modifier>=0?(modifier<10?" +":"+"):(modifier<-9?"":" "))+modifier+
				" = "+tempVal;
	}

	public String toString(){
		return 		 (attr[0]<10?" ":"")+attr[0]
				+":"+(attr[1]<10?" ":"")+attr[1]
				+":"+(attr[2]<10?" ":"")+attr[2]
				+" ["+(value<10?" ":"")+value
				+"] "+name;
	}*/

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Talent talent = (Talent) o;

		return name.equals(talent.name);
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
