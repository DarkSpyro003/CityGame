<?php
class Question implements JsonSerializable
{
	private $type;
	private $question;
	private $text_answer;
	private $multi_answer;
	private $options = array();
	private $extraInfo;
	private $contentUrl;
	private $latitude;
	private $longitude;
	
	public function __construct()
	{
		$argv = func_get_args();
		switch( func_num_args() ) 
		{
			case 3:
				self::__construct3($argv[0], $argv[1], $argv[2]);
				break;
			case 4:
				self::__construct4($argv[0], $argv[1], $argv[2], $argv[3]);
				break;
			default:
				throw new Exception('Invalid constructor called.');
		}
	}
	
	// Constructor plain text question
	public function __construct3($type, $question, $answer)
	{
		if( $type != 0 )
			throw new Exception('Type must be 0 for using this constructor');
		
		$this->type = $type;
		$this->question = $question;
		$this->text_answer = $answer;
	}
	
	// Constructor multiple choice question
	public function __construct4($type, $question, $answer, $options)
	{
		if( $type != 1 )
			throw new Exception('Type must be 1 for using this constructor');
		
		$this->type = $type;
		$this->question = $question;
		$this->multi_answer = $answer;
		$this->options = $options;
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