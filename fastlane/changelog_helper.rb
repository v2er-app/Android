# changelog_helper.rb
# Helper module to extract changelog entries from CHANGELOG.md

require 'fastlane_core/ui/ui'

module ChangelogHelper
  # Import Fastlane's UI for logging
  UI = FastlaneCore::UI unless defined?(UI)

  # Google Play whatsnew character limit
  GOOGLE_PLAY_LIMIT = 500

  # GitHub Release notes limit (generous)
  GITHUB_RELEASE_LIMIT = 10000

  # Get the current version from config.gradle
  # @return [String] The current version name
  def self.get_current_version
    config_path = File.expand_path("../config.gradle", __dir__)

    unless File.exist?(config_path)
      UI.user_error!("config.gradle not found at #{config_path}")
    end

    content = File.read(config_path)
    version_match = content.match(/versionName:\s*"([^"]+)"/)

    if version_match
      version = version_match[1]
      UI.message("Current version from config.gradle: #{version}")
      version
    else
      UI.user_error!("Could not find versionName in config.gradle")
    end
  end

  # Get the current version code from config.gradle
  # @return [Integer] The current version code
  def self.get_current_version_code
    config_path = File.expand_path("../config.gradle", __dir__)

    unless File.exist?(config_path)
      UI.user_error!("config.gradle not found at #{config_path}")
    end

    content = File.read(config_path)
    code_match = content.match(/versionCode:\s*(\d+)/)

    if code_match
      code = code_match[1].to_i
      UI.message("Current version code from config.gradle: #{code}")
      code
    else
      UI.user_error!("Could not find versionCode in config.gradle")
    end
  end

  # Get all versions from CHANGELOG.md in order (newest first)
  # @return [Array<String>] List of version strings
  def self.get_all_versions
    changelog_path = File.expand_path("../CHANGELOG.md", __dir__)

    unless File.exist?(changelog_path)
      UI.error("CHANGELOG.md not found at #{changelog_path}")
      return []
    end

    content = File.read(changelog_path)
    versions = []

    content.each_line do |line|
      if match = line.match(/^##\s+v?(\d+\.\d+\.\d+)/)
        versions << match[1]
      end
    end

    versions
  end

  # Extract raw changelog content for a version (without formatting)
  # @param version [String] The version to extract
  # @return [String, nil] Raw changelog content or nil if not found
  def self.extract_raw_changelog(version)
    changelog_path = File.expand_path("../CHANGELOG.md", __dir__)

    unless File.exist?(changelog_path)
      return nil
    end

    content = File.read(changelog_path)
    # Match version with or without 'v' prefix and optional build number
    version_pattern = /^##\s+v?#{Regexp.escape(version)}(?:\s+\(Build\s+\w+\))?\s*$/

    lines = content.lines
    start_index = nil
    end_index = nil

    lines.each_with_index do |line, index|
      if line.match?(version_pattern)
        start_index = index
        break
      end
    end

    return nil if start_index.nil?

    ((start_index + 1)...lines.length).each do |index|
      line = lines[index]
      if line.match?(/^##\s+/) || line.match?(/^---/)
        end_index = index
        break
      end
    end

    end_index ||= lines.length

    changelog_lines = lines[(start_index + 1)...end_index]
    changelog_lines.join("").strip
  end

  # Extract changelog for a specific version from CHANGELOG.md
  # @param version [String] The version to extract (e.g., "2.3.14")
  # @return [String] The changelog content for the specified version
  def self.extract_changelog(version)
    changelog_path = File.expand_path("../CHANGELOG.md", __dir__)

    unless File.exist?(changelog_path)
      UI.error("CHANGELOG.md not found at #{changelog_path}")
      return "Bug fixes and improvements"
    end

    raw_content = extract_raw_changelog(version)

    if raw_content.nil? || raw_content.empty?
      UI.important("No changelog content found for version #{version}")
      UI.message("Available versions:")
      get_all_versions.each do |v|
        UI.message("  - v#{v}")
      end
      return "Bug fixes and improvements"
    end

    UI.success("Extracted changelog for version #{version}:")
    UI.message(raw_content)

    raw_content
  end

  # Format changelog content for Google Play (bullet points, character limit)
  # @param content [String] Raw changelog content
  # @return [String] Formatted changelog for Google Play
  def self.format_for_google_play(content)
    # Convert numbered lists to bullet points
    # "1. Feature: xxx" -> "• xxx"
    formatted = content.gsub(/^\d+\.\s+(Feature|Fix|Improvement|Breaking):\s*/, "• ")

    formatted
  end

  # Get changelog for current version and up to 2 previous versions (3 versions total)
  # Combined length respects Google Play's 500 character limit
  # @return [String] Combined changelog for Google Play
  def self.get_current_changelog_for_google_play
    current_version = get_current_version
    all_versions = get_all_versions

    # Find current version index
    current_index = all_versions.index(current_version)

    if current_index.nil?
      UI.important("Current version #{current_version} not found in CHANGELOG.md")
      return "Bug fixes and improvements"
    end

    # Get up to 3 versions (current + 2 previous)
    versions_to_include = all_versions[current_index, 3] || [current_version]

    # Build combined changelog
    combined_parts = []
    # Use actual newline characters so length calculation is accurate
    feedback_header = "Feedback: https://v2er.app/help\n\n"

    # Reserve space for feedback header (using actual string length)
    available_space = GOOGLE_PLAY_LIMIT - feedback_header.length

    versions_to_include.each_with_index do |version, index|
      raw_content = extract_raw_changelog(version)
      next if raw_content.nil? || raw_content.empty?

      # Format the content (convert to bullet points)
      formatted_content = format_for_google_play(raw_content)

      # Add version header for older versions
      if index == 0
        version_section = formatted_content
      else
        version_section = "\n--- v#{version} ---\n#{formatted_content}"
      end

      # Check if adding this section would exceed the limit (use <= to include content at exactly the limit)
      current_length = combined_parts.join.length
      if current_length + version_section.length <= available_space
        combined_parts << version_section
      else
        break
      end
    end

    if combined_parts.empty?
      return feedback_header + "Bug fixes and improvements"
    end

    result = feedback_header + combined_parts.join

    UI.success("Combined changelog for #{versions_to_include.length} version(s)")
    UI.message("Total length: #{result.length} / #{GOOGLE_PLAY_LIMIT} characters")

    result
  end

  # Get changelog for GitHub Release (markdown format, less restrictive limit)
  # @return [String] Changelog formatted for GitHub Release
  def self.get_current_changelog_for_github
    current_version = get_current_version
    raw_content = extract_raw_changelog(current_version)

    if raw_content.nil? || raw_content.empty?
      return "Bug fixes and improvements"
    end

    # Format as markdown
    # Keep the numbered list format for GitHub
    formatted = raw_content.lines.map do |line|
      if line.match?(/^\d+\.\s+(Feature):/i)
        line.gsub(/^\d+\.\s+Feature:\s*/i, "- **Feature**: ")
      elsif line.match?(/^\d+\.\s+(Fix):/i)
        line.gsub(/^\d+\.\s+Fix:\s*/i, "- **Fix**: ")
      elsif line.match?(/^\d+\.\s+(Improvement):/i)
        line.gsub(/^\d+\.\s+Improvement:\s*/i, "- **Improvement**: ")
      elsif line.match?(/^\d+\.\s+(Breaking):/i)
        line.gsub(/^\d+\.\s+Breaking:\s*/i, "- **Breaking**: ")
      else
        line
      end
    end.join

    formatted.strip
  end

  # Validate that changelog exists for the current version
  # @return [Boolean] True if changelog exists, false otherwise
  def self.validate_changelog_exists
    current_version = get_current_version
    changelog_path = File.expand_path("../CHANGELOG.md", __dir__)

    unless File.exist?(changelog_path)
      UI.error("❌ CHANGELOG.md not found!")
      UI.message("Please create CHANGELOG.md with an entry for version #{current_version}")
      return false
    end

    content = File.read(changelog_path)
    version_pattern = /^##\s+v?#{Regexp.escape(current_version)}/

    if content.match?(version_pattern)
      UI.success("✅ Changelog entry found for version #{current_version}")
      return true
    else
      UI.error("❌ No changelog entry found for version #{current_version}")
      UI.message("Please add a changelog entry in CHANGELOG.md:")
      UI.message("")
      UI.message("## v#{current_version}")
      UI.message("1. Feature/Fix: Description of changes")
      UI.message("")
      return false
    end
  end
end
