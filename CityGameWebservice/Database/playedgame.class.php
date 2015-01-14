<?php
class PlayedGame implements JsonSerializable
{
	private $gameContentId;
	private $score;
	
	public function __construct($gameContentId, $score)
	{
		$this->gameContentId = $gameContentId;
		$this->score = $score;
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