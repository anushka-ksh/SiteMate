package com.example.webchecker;

import org.springframework.web.bind.annotation.PathVariable; // NEW
import java.security.Principal; // Make sure this is imported
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebCheckerController {

	// All our repositories and services
	private final UptimeCheckService uptimeCheckService;
	private final CheckResultRepository resultRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final MonitoredSiteRepository siteRepository; // NEW

	@Autowired
	public WebCheckerController(UptimeCheckService uptimeCheckService,
			CheckResultRepository resultRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			MonitoredSiteRepository siteRepository) { // NEW
		this.uptimeCheckService = uptimeCheckService;
		this.resultRepository = resultRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.siteRepository = siteRepository; // NEW
	}

	// --- NEW: Admin Dashboard ---
	// --- UPDATED: Admin Dashboard ---
	@GetMapping("/admin/dashboard")
	public String showAdminDashboard(Model model) {

		// 1. Fetch ALL users from the database
		List<User> allUsers = userRepository.findAll();

		// 2. --- NEW: Fetch Statistics ---
		long totalUsers = userRepository.count();
		long totalSites = siteRepository.count();
		long totalChecks = resultRepository.count();

		// 3. Add all data to the model
		model.addAttribute("users", allUsers);
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalSites", totalSites);
		model.addAttribute("totalChecks", totalChecks);

		return "admin-dashboard"; // Renders admin-dashboard.html
	}

	// --- NEW METHOD: Show Site Details Page ---
	@GetMapping("/dashboard/site/{id}")
	public String showSiteDetails(@PathVariable("id") Long id, Model model, Principal principal) {
		// Get the logged-in user
		User user = getUserFromPrincipal(principal);

		// Find the site by its ID
		MonitoredSite site = siteRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid site Id:" + id));

		// SECURITY CHECK: Make sure the site belongs to the logged-in user
		if (!site.getUser().getId().equals(user.getId())) {
			return "redirect:/dashboard?error=auth";
		}

		// Add the site info to the model
		model.addAttribute("site", site);

		// Find the history for this site's URL
		List<CheckResult> history = resultRepository.findTop20ByUrlOrderByTimestampDesc(site.getUrl());
		model.addAttribute("history", history);

		return "site-details"; // Renders a new file: site-details.html
	}

	// --- NEW METHOD: Delete a Site ---
	@PostMapping("/dashboard/deletesite/{id}")
	public String deleteMonitoredSite(@PathVariable("id") Long id, Principal principal) {
		// Get the logged-in user
		User user = getUserFromPrincipal(principal);

		// Find the site by its ID
		MonitoredSite site = siteRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid site Id:" + id));

		// SECURITY CHECK: Make sure the site actually belongs to the logged-in user
		if (!site.getUser().getId().equals(user.getId())) {
			// If not, it's a security violation. Redirect them.
			return "redirect:/dashboard?error=auth";
		}

		// We also need to delete the history for this site first (optional but good
		// practice)
		List<CheckResult> history = resultRepository.findTop20ByUrlOrderByTimestampDesc(site.getUrl());
		resultRepository.deleteAll(history); // Delete the associated check results

		// Delete the site
		siteRepository.delete(site);

		// Redirect back to the dashboard
		return "redirect:/dashboard";
	}

	// --- Homepage (No Changes) ---
	@GetMapping("/")
	public String showHomePage() {
		return "index";
	}

	@PostMapping("/check")
	public String handleCheckRequest(@RequestParam("url") String url, Model model) {
		// ... (no changes here)
		String statusMessage = uptimeCheckService.checkUrlStatus(url);
		model.addAttribute("statusMessage", statusMessage);
		model.addAttribute("isUp", !statusMessage.startsWith("[DOWN]"));
		List<CheckResult> history = resultRepository.findTop20ByUrlOrderByTimestampDesc(url);
		model.addAttribute("history", history);
		return "index";
	}

	// --- Auth Pages (No Changes) ---
	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}

	@GetMapping("/signup")
	public String showSignUpForm(Model model) {
		model.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/signup")
	public String processSignUp(@ModelAttribute User user) {
		// ... (no changes here)
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			return "redirect:/login?error=username";
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole("USER");
		userRepository.save(user);
		return "redirect:/login?success";
	}

	// --- UPDATED DASHBOARD METHOD ---
	@GetMapping("/dashboard")
	public String showDashboard(Model model, Principal principal) {
		// Get the logged-in user's object
		User user = getUserFromPrincipal(principal);

		// Add username to the model
		model.addAttribute("username", user.getUsername());

		// Create an empty site object for the "Add New Site" form
		model.addAttribute("newSite", new MonitoredSite());

		// Fetch all sites owned by this user
		List<MonitoredSite> userSites = siteRepository.findByUser(user);
		model.addAttribute("userSites", userSites);

		return "dashboard"; // Renders dashboard.html
	}

	// --- NEW METHOD: Add a Site ---
	@PostMapping("/dashboard/addsite")
	public String addMonitoredSite(@ModelAttribute MonitoredSite newSite, Principal principal) {
		// Get the logged-in user
		User user = getUserFromPrincipal(principal);

		// Link the new site to this user
		newSite.setUser(user);

		// Save the site to the database
		siteRepository.save(newSite);

		// Redirect back to the dashboard to see the new list
		return "redirect:/dashboard";
	}

	// --- NEW HELPER METHOD ---
	// A private method to get the User object from the security principal
	private User getUserFromPrincipal(Principal principal) {
		String username = principal.getName();
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
						"User not found"));
	}
}