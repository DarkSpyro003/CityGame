<?php
require_once 'question.class.php';

class GameContent implements JsonSerializable
{
	private $title;
	private $questionList = array();
	
	public function __construct($title, $questionList)
	{
		$this->title = $title;
		$this->questionList = $questionList;
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