@CrossOrigin
@Controller
@RequestMapping(value = "/mainApp")
public class MainAppController {
  
  @RequestMapping(value = "/autoDeleteTodoList", method = { RequestMethod.GET, RequestMethod.POST})
	public String autoDeleteTodoList() {
		return "pages/autoDeleteTodoList";
	}
  
}
  
