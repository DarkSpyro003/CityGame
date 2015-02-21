<?php
class PlayedGame implements JsonSerializable
{
	private $gameContentId;
	private $score;
	private $questionAnswerData;
	
	public function __construct($gameContentId, $score, $questionAnswerData)
	{
		$this->gameContentId = $gameContentId;
		$this->score = $score;
		$this->questionAnswerData = $questionAnswerData;
	}
	
	public function jsonSerialize()
	{
		return get_object_vars($this);
	}
	
	public function __get($property) 
	{
		if (property_exists($this, $property))
		{
			return $this->$property;
		}
	}

	public function __set($property, $value)
	{
		if (property_exists($this, $property)) 
		{
			$this->$property = $value;
		}
		return $this;
	}
}
?>