<?php
require_once('playedgame.class.php');

class Player implements JsonSerializable
{
	private $id;
	private $username;
	private $email;
	private $realname;
	private $games = array();
	
	public function __construct($username, $email, $realname)
	{
		$this->username = $username;
		$this->email = $email;
		$this->realname = $realname;
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