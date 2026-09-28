package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating the onboarding guide completion status in user settings.
 *
 * @property repository Repository for managing app settings.
 */
class CompleteOnboardingUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    /**
     * Updates the onboarding guide completion status in the settings repository.
     *
     * @param hasSeen True if the user has completed/skipped the guide, false otherwise.
     */
    suspend operator fun invoke(hasSeen: Boolean = true) {
        repository.setHasSeenOnboarding(hasSeen)
    }
}
