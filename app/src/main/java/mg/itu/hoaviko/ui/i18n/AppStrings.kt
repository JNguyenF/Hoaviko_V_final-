package mg.itu.hoaviko.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf

/** Tous les textes de l'interface, localisés en FR / EN / MG. */
data class AppStrings(
    val appName: String,
    val tagline: String,
    // Commun
    val save: String,
    val cancel: String,
    val ok: String,
    val back: String,
    val currency: String,
    // Authentification
    val introTitle: String,
    val introParagraph1: String,
    val introParagraph2: String,
    val introParagraph3: String,
    val loginButton: String,
    val registerButton: String,
    val loginScreenTitle: String,
    val identifierLabel: String,
    val passwordLabel: String,
    val loginSubmit: String,
    val forgotPassword: String,
    val registerLink: String,
    val loginSuccess: String,
    val registerScreenTitle: String,
    val lastNameLabel: String,
    val firstNameLabel: String,
    val birthDateLabel: String,
    val cinLabel: String,
    val professionLabel: String,
    val emailLabel: String,
    val createPasswordLabel: String,
    val confirmPasswordLabel: String,
    val registerSubmit: String,
    val registerSuccess: String,
    val forgotTitle: String,
    val forgotSubtitle: String,
    val forgotEmailLabel: String,
    val forgotCinLabel: String,
    val newPasswordLabel: String,
    val confirmNewPasswordLabel: String,
    val forgotSubmit: String,
    val resetSuccess: String,
    // Accueil
    val greeting: String,
    val memberRank: String,
    val objectiveLabel: String,
    val objectiveProgress: String,
    val capitalProjected: String,
    val totalPaid: String,
    val paymentsCount: String,
    val monthlyContribution: String,
    val simulateButton: String,
    val editSettingsButton: String,
    val defineObjectiveHint: String,
    // Simulation
    val simTitle: String,
    val simSubtitle: String,
    val ageNowLabel: String,
    val ageRetirementLabel: String,
    val monthlyLabel: String,
    val initialLabel: String,
    val rateLabel: String,
    val institutionLabel: String,
    val resultTitle: String,
    val applyButton: String,
    val invalidInputHint: String,
    // Suivi
    val trackTitle: String,
    val newDepositTitle: String,
    val amountLabel: String,
    val paymentModeLabel: String,
    val dateLabel: String,
    val noteLabel: String,
    val depositButton: String,
    val passwordConfirmLabel: String,
    val depositDialogTitle: String,
    val depositConfirmHint: String,
    val depositSuccessTitle: String,
    val depositSuccessLine: String,
    val historyTitle: String,
    val filterAll: String,
    val filterWeek: String,
    val filterMonth: String,
    val filterYear: String,
    val emptyHistory: String,
    val periodNoResult: String,
    val chooseChannel: String,
    val phoneNumberLabel: String,
    val bankAccountLabel: String,
    // Profil / Réglages
    val settingsTitle: String,
    val usernameLabel: String,
    val fullNameLabel: String,
    val professionValueLabel: String,
    val languageLabel: String,
    val langFr: String,
    val langEn: String,
    val langMg: String,
    val savingsBankLabel: String,
    val objectiveField: String,
    val monthlyField: String,
    val saveSettingsButton: String,
    val currentPasswordLabel: String,
    val changePasswordButton: String,
    val changePasswordConfirmTitle: String,
    val changePasswordConfirmHint: String,
    val retirementInfo: String,
    val logoutButton: String,
    val logoutMessage: String,
    // Demande de retrait
    val withdrawalButton: String,
    val withdrawalDialogTitle: String,
    val withdrawalConfirmHint: String,
    val noticeFieldLabel: String,
    val noticeWithLabel: String,
    val noticeWithoutLabel: String,
    val penaltyPreview: String,
    val justificationLabel: String,
    val justificationHint: String,
    val justificationLine: String,
    val msgJustificationRequired: String,
    val withdrawalSubmitButton: String,
    val myWithdrawalRequests: String,
    val emptyWithdrawals: String,
    val statusPending: String,
    val statusApproved: String,
    val statusRejected: String,
    val requestedOn: String,
    val withdrawnOn: String,
    val penaltyLine: String,
    val netLine: String,
    val withdrawalSuccessTitle: String,
    val withdrawalSuccessLine: String,
    // Administration
    val adminTitle: String,
    val adminRequestsTitle: String,
    val emptyAdminRequests: String,
    val approveButton: String,
    val rejectButton: String,
    val approveConfirmTitle: String,
    val approveConfirmHint: String,
    val rejectConfirmTitle: String,
    val rejectConfirmHint: String,
    val msgWithdrawalApproved: String,
    val msgWithdrawalRejected: String,
    // Administration - gestion des utilisateurs
    val adminTabUsers: String,
    val emptyAdminUsers: String,
    val deleteUserButton: String,
    val deleteUserConfirmTitle: String,
    val deleteUserConfirmHint: String,
    val deleteReasonLabel: String,
    val deleteReasonLine: String,
    val msgUserDeleted: String,
    // Politique d'utilisation
    val acceptPolicyLabel: String,
    val readPolicyLink: String,
    val policyTitle: String,
    val policyBody: String,
    val msgAcceptPolicy: String,
    // Messages de validation
    val msgFillAllFields: String,
    val msgInvalidEmail: String,
    val msgEmailTaken: String,
    val msgPasswordShort: String,
    val msgPasswordMismatch: String,
    val msgInvalidCredentials: String,
    val msgAccountNotFound: String,
    val msgCinMismatch: String,
    val msgSettingsSaved: String,
    val msgInvalidAmount: String,
    val msgWrongPassword: String,
    val msgPasswordChanged: String,
    val msgInsufficientBalance: String,
    val msgInternetRequired: String,
    val msgOnlineRegistrationFailed: String
)

/** Messages d'état remontés par les ViewModels, traduits côté UI. */
enum class UiMessage {
    LOGIN_SUCCESS, REGISTER_SUCCESS, RESET_SUCCESS, SETTINGS_SAVED, PASSWORD_CHANGED,
    FILL_ALL_FIELDS, INVALID_EMAIL, EMAIL_TAKEN, PASSWORD_SHORT,
    PASSWORD_MISMATCH, INVALID_CREDENTIALS, ACCOUNT_NOT_FOUND,
    CIN_MISMATCH, INVALID_AMOUNT, WRONG_PASSWORD, LOGOUT,
    WITHDRAWAL_APPROVED, WITHDRAWAL_REJECTED, USER_DELETED,
    ACCEPT_POLICY_REQUIRED, INSUFFICIENT_BALANCE, JUSTIFICATION_REQUIRED,INTERNET_REQUIRED,
    ONLINE_REGISTRATION_FAILED
}

fun UiMessage.text(strings: AppStrings): String = when (this) {
    UiMessage.LOGIN_SUCCESS -> strings.loginSuccess
    UiMessage.REGISTER_SUCCESS -> strings.registerSuccess
    UiMessage.RESET_SUCCESS -> strings.resetSuccess
    UiMessage.SETTINGS_SAVED -> strings.msgSettingsSaved
    UiMessage.PASSWORD_CHANGED -> strings.msgPasswordChanged
    UiMessage.FILL_ALL_FIELDS -> strings.msgFillAllFields
    UiMessage.INVALID_EMAIL -> strings.msgInvalidEmail
    UiMessage.EMAIL_TAKEN -> strings.msgEmailTaken
    UiMessage.PASSWORD_SHORT -> strings.msgPasswordShort
    UiMessage.PASSWORD_MISMATCH -> strings.msgPasswordMismatch
    UiMessage.INVALID_CREDENTIALS -> strings.msgInvalidCredentials
    UiMessage.ACCOUNT_NOT_FOUND -> strings.msgAccountNotFound
    UiMessage.CIN_MISMATCH -> strings.msgCinMismatch
    UiMessage.INVALID_AMOUNT -> strings.msgInvalidAmount
    UiMessage.WRONG_PASSWORD -> strings.msgWrongPassword
    UiMessage.LOGOUT -> strings.logoutMessage
    UiMessage.WITHDRAWAL_APPROVED -> strings.msgWithdrawalApproved
    UiMessage.WITHDRAWAL_REJECTED -> strings.msgWithdrawalRejected
    UiMessage.USER_DELETED -> strings.msgUserDeleted
    UiMessage.ACCEPT_POLICY_REQUIRED -> strings.msgAcceptPolicy
    UiMessage.INSUFFICIENT_BALANCE -> strings.msgInsufficientBalance
    UiMessage.JUSTIFICATION_REQUIRED -> strings.msgJustificationRequired
    UiMessage.INTERNET_REQUIRED ->
        strings.msgInternetRequired

    UiMessage.ONLINE_REGISTRATION_FAILED ->
        strings.msgOnlineRegistrationFailed
}

/** Remplace les marqueurs {clé} par leur valeur. */
fun fmt(template: String, vararg args: Pair<String, Any>): String {
    var result = template
    args.forEach { (key, value) -> result = result.replace("{$key}", value.toString()) }
    return result
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { FrenchStrings }

private val currencyAr = "Ar"

val FrenchStrings = AppStrings(
    appName = "HOAVIKO",
    tagline = "Épargne retraite des travailleurs indépendants à Madagascar",
    save = "Enregistrer",
    cancel = "Annuler",
    ok = "OK",
    back = "Retour",
    currency = currencyAr,
    introTitle = "Préparez votre retraite",
    introParagraph1 = "HOAVIKO aide les travailleurs indépendants malgaches à constituer une épargne retraite auprès d'une banque ou d'une institution de microfinance.",
    introParagraph2 = "Simulez l'évolution de votre épargne, effectuez vos versements réguliers et suivez votre capital jusqu'à vos 60 ans.",
    introParagraph3 = "Votre épargne reste accessible dès 50 ans. Inscrivez-vous gratuitement pour commencer.",
    loginButton = "Se connecter",
    registerButton = "Créer un compte",
    loginScreenTitle = "Connexion",
    identifierLabel = "Nom d'utilisateur ou e-mail",
    passwordLabel = "Mot de passe",
    loginSubmit = "Se connecter",
    forgotPassword = "Mot de passe oublié ?",
    registerLink = "Créer un compte",
    loginSuccess = "Connexion établie",
    registerScreenTitle = "Création de compte",
    lastNameLabel = "Nom",
    firstNameLabel = "Prénoms",
    birthDateLabel = "Date de naissance",
    cinLabel = "Numéro CIN",
    professionLabel = "Profession",
    emailLabel = "E-mail",
    createPasswordLabel = "Créer un mot de passe",
    confirmPasswordLabel = "Confirmer le mot de passe",
    registerSubmit = "S'inscrire",
    registerSuccess = "Compte créé avec succès !",
    forgotTitle = "Réinitialiser le mot de passe",
    forgotSubtitle = "Renseignez votre e-mail et votre numéro CIN pour vérifier votre identité.",
    forgotEmailLabel = "E-mail du compte",
    forgotCinLabel = "Numéro CIN",
    newPasswordLabel = "Nouveau mot de passe",
    confirmNewPasswordLabel = "Confirmer le nouveau mot de passe",
    forgotSubmit = "Réinitialiser",
    resetSuccess = "Mot de passe réinitialisé. Connectez-vous !",
    greeting = "Salut, {name} !",
    memberRank = "Membre n°{rank}",
    objectiveLabel = "Mon objectif",
    objectiveProgress = "{current} / {target}",
    capitalProjected = "Capital projeté à {age} ans",
    totalPaid = "Totalité versée",
    paymentsCount = "Nombre de versements",
    monthlyContribution = "Cotisation mensuelle",
    simulateButton = "Simuler un nouveau plan",
    editSettingsButton = "Paramétrer mon compte",
    defineObjectiveHint = "Définissez votre objectif dans Mon profil pour suivre votre progression.",
    simTitle = "Simulation d'épargne",
    simSubtitle = "Chiffrez votre capital de retraite en fonction de vos cotisations.",
    ageNowLabel = "Âge actuel",
    ageRetirementLabel = "Âge de retraite",
    monthlyLabel = "Cotisation mensuelle",
    initialLabel = "Épargne initiale",
    rateLabel = "Taux annuel",
    institutionLabel = "Établissement d'épargne",
    resultTitle = "Capital estimé sur {years} ans",
    applyButton = "Appliquer à mon profil",
    invalidInputHint = "Renseignez des valeurs valides (âge de retraite > âge actuel, cotisation > 0).",
    trackTitle = "Espace compte · Suivi",
    newDepositTitle = "Verser maintenant",
    amountLabel = "Montant",
    paymentModeLabel = "Mode de paiement",
    dateLabel = "Date",
    noteLabel = "Note (optionnel)",
    depositButton = "Confirmer le versement",
    passwordConfirmLabel = "Confirmer avec votre mot de passe",
    depositDialogTitle = "Confirmation du versement",
    depositConfirmHint = "Une fois le versement confirmé, l'opération est définitive et ne peut pas être annulée.",
    depositSuccessTitle = "Versement effectué, Félicitations !",
    depositSuccessLine = "Vous avez {amount}. Totalité à jour : {total}.",
    historyTitle = "Historique des versements",
    filterAll = "Total",
    filterWeek = "Semaine",
    filterMonth = "Mois",
    filterYear = "Année",
    emptyHistory = "Aucun versement pour le moment.",
    periodNoResult = "Aucun versement sur cette période.",
    chooseChannel = "Sélectionner",
    phoneNumberLabel = "Numéro de téléphone",
    bankAccountLabel = "Numéro de compte bancaire",
    settingsTitle = "Mon profil",
    usernameLabel = "Nom d'utilisateur",
    fullNameLabel = "Nom et prénoms",
    professionValueLabel = "Profession",
    languageLabel = "Langue",
    langFr = "Français",
    langEn = "Anglais",
    langMg = "Malgache",
    savingsBankLabel = "Établissement d'épargne",
    objectiveField = "Objectif d'épargne (Ar)",
    monthlyField = "Cotisation mensuelle visée (Ar)",
    saveSettingsButton = "Enregistrer",
    currentPasswordLabel = "Mot de passe actuel",
    changePasswordButton = "Changer le mot de passe",
    changePasswordConfirmTitle = "Confirmer le changement de mot de passe",
    changePasswordConfirmHint = "Voulez-vous vraiment modifier votre mot de passe ?",
    retirementInfo = "Âge de retraite : {age} ans. Épargne accessible dès {access} ans.",
    logoutButton = "Se déconnecter",
    logoutMessage = "Vous êtes déconnecté.",
    withdrawalButton = "Faire un retrait",
    withdrawalDialogTitle = "Demande de retrait d'épargne",
    withdrawalConfirmHint = "La demande sera envoyée au gestionnaire HOAVIKO pour validation. Vous ne pourrez pas l'annuler.",
    noticeFieldLabel = "Type de retrait",
    noticeWithLabel = "Avec préavis (7 j)",
    noticeWithoutLabel = "Immédiat",
    penaltyPreview = "Pénalité HOAVIKO : {percent} % ({amount})",
    justificationLabel = "Justification du retrait",
    justificationHint = "Ex. : frais de santé, scolarité, besoin urgent…",
    justificationLine = "Justification : {reason}",
    withdrawalSubmitButton = "Envoyer la demande",
    myWithdrawalRequests = "Mes demandes de retrait",
    emptyWithdrawals = "Aucune demande de retrait.",
    statusPending = "En attente",
    statusApproved = "Approuvée",
    statusRejected = "Refusée",
    requestedOn = "Demande le {date}",
    withdrawnOn = "Retrait le {date}",
    penaltyLine = "Pénalité : {percent} % ({amount})",
    netLine = "Net reçu : {amount}",
    withdrawalSuccessTitle = "Demande envoyée",
    withdrawalSuccessLine = "Retrait de {amount} · Pénalité {penalty} ({percent} %) · Net reçu {net}.",
    adminTitle = "Administration HOAVIKO",
    adminRequestsTitle = "Demandes de retrait reçues",
    emptyAdminRequests = "Aucune demande de retrait pour le moment.",
    approveButton = "Approuver",
    rejectButton = "Refuser",
    approveConfirmTitle = "Approuver la demande",
    approveConfirmHint = "Valider le retrait de {amount} ?",
    rejectConfirmTitle = "Refuser la demande",
    rejectConfirmHint = "Refuser le retrait de {amount} ?",
    msgWithdrawalApproved = "Demande approuvée.",
    msgWithdrawalRejected = "Demande refusée.",
    adminTabUsers = "Utilisateurs",
    emptyAdminUsers = "Aucun utilisateur inscrit.",
    deleteUserButton = "Supprimer",
    deleteUserConfirmTitle = "Supprimer le compte",
    deleteUserConfirmHint = "Supprimer définitivement le compte de {name} ({username}) ?",
    deleteReasonLabel = "Raison de la suppression",
    deleteReasonLine = "Raison : {reason}",
    msgUserDeleted = "Compte supprimé.",
    acceptPolicyLabel = "J'accepte la politique d'utilisation",
    readPolicyLink = "Lire la politique d'utilisation",
    policyTitle = "Politique d'utilisation",
    policyBody = "Politique d'utilisation — HOAVIKO\n" +
        "Dernière mise à jour : 15/09/2026\n\n" +
        "1. Objet du service\n" +
        "HOAVIKO est une application d'épargne retraite destinée aux travailleurs indépendants malgaches. Elle permet de simuler l'évolution de son épargne, d'effectuer des versements réguliers, de suivre son capital et d'effectuer des retraits, jusqu'à l'âge de la retraite fixé à 60 ans. L'application est purement informative et locale : elle ne constitue ni un établissement financier, ni un conseiller en investissement, et ne garantit aucun rendement.\n\n" +
        "2. Acceptation des conditions\n" +
        "Toute inscription sur HOAVIKO vaut acceptation de la présente politique. L'utilisateur est invité à la lire avant de créer son compte.\n\n" +
        "3. Compte utilisateur\n" +
        "L'utilisateur s'engage à fournir des informations exactes et à jour : nom, prénoms, numéro CIN, e-mail et date de naissance.\n" +
        "Un seul compte est autorisé par personne.\n" +
        "L'utilisateur est seul responsable de la confidentialité de son identifiant et de son mot de passe, ainsi que de toute activité réalisée sur son compte.\n" +
        "4. Confidentialité et stockage des données\n" +
        "Les données sont stockées localement sur l'appareil : l'application fonctionne entièrement hors ligne, ne collecte ni ne partage aucune donnée personnelle vers un serveur.\n" +
        "Les mots de passe sont hachés (SHA-256 + sel) et ne sont jamais enregistrés en clair.\n" +
        "L'utilisateur peut consulter et modifier ses informations via l'écran « Mon profil ».\n" +
        "5. Simulation et projections\n" +
        "Les résultats de simulation (capital projeté, taux annuels) sont indicatifs : ils ne constituent pas une promesse de rendement ni un conseil financier contractuel.\n\n" +
        "6. Versements\n" +
        "Un versement devient définitif après confirmation par le mot de passe de l'utilisateur.\n" +
        "L'historique des versements est non supprimable.\n" +
        "Le montant unitaire est limité selon le canal choisi (Mobile Money ou banque).\n" +
        "L'épargne est normalement accessible à partir de 50 ans.\n" +
        "7. Retraits\n" +
        "Le retrait d'une partie de l'épargne est possible sous réserve des règles suivantes :\n\n" +
        "Âge au retrait\tPréavis d'au moins 7 jours\tPénalité HOAVIKO (sur le montant retiré)\n" +
        "Moins de 50 ans\tNon\t10 %\n" +
        "Moins de 50 ans\tOui\t5 %\n" +
        "50 ans et plus\t—\t0 %\n" +
        "Le préavis est constitué par une demande de retrait enregistrée dans l'application au moins 7 jours calendaires avant la date de retrait souhaitée. La demande est datée et tracée.\n" +
        "En l'absence de préavis, un retrait anticipé (avant 50 ans) est soumis à une pénalité de 10 % du montant retiré.\n" +
        "Avec un préavis respecté, la pénalité est ramenée à 5 % du montant retiré.\n" +
        "À partir de 50 ans (y compris jusqu'à la retraite à 60 ans et au-delà), les retraits sont exempts de pénalité.\n" +
        "Le montant net perçu = montant retiré − pénalité.\n" +
        "Le retrait est définitif et irréversible après confirmation par mot de passe ; son historique est non supprimable.\n" +
        "8. Restrictions d'usage\n" +
        "Sont strictement interdits : l'usurpation d'identité, les fausses déclarations (notamment d'âge ou de préavis), ainsi que toute utilisation frauduleuse du service.\n" +
        "9. Responsabilité\n" +
        "HOAVIKO ne saurait être tenu responsable de la perte de données résultant d'une désinstallation, d'une réinitialisation de l'appareil ou d'une absence de sauvegarde. Les taux, pénalités et projections affichés sont susceptibles d'évoluer.\n" +
        "10. Suspension de compte\n" +
        "Un compte peut être suspendu ou fermé par l'administrateur en cas de non-respect de la présente politique.\n" +
        "11. Modifications\n" +
        "La présente politique peut être mise à jour. La version en vigueur est celle affichée dans l'application.",
    msgAcceptPolicy = "Veuillez accepter la politique d'utilisation pour créer un compte.",
    msgFillAllFields = "Veuillez remplir tous les champs.",
    msgInvalidEmail = "Adresse e-mail invalide.",
    msgEmailTaken = "Un compte existe déjà avec cet e-mail.",
    msgPasswordShort = "Le mot de passe doit contenir au moins 6 caractères.",
    msgPasswordMismatch = "Les mots de passe ne correspondent pas.",
    msgInvalidCredentials = "Identifiant ou mot de passe incorrect.",
    msgAccountNotFound = "Aucun compte trouvé avec cet e-mail.",
    msgCinMismatch = "Le numéro CIN ne correspond pas à ce compte.",
    msgSettingsSaved = "Paramètres enregistrés.",
    msgInvalidAmount = "Saisissez un montant valide.",
    msgWrongPassword = "Mot de passe incorrect.",
    msgPasswordChanged = "Mot de passe modifié avec succès.",
msgInsufficientBalance = "Solde insuffisant, merci de vérifier votre montant.",
    msgJustificationRequired = "Veuillez préciser la justification de votre retrait.",
    msgInternetRequired = "Une connexion Internet est obligatoire pour créer un compte.",
    msgOnlineRegistrationFailed = "La création du compte en ligne a échoué. Veuillez réessayer."
)

val EnglishStrings = AppStrings(
    appName = "HOAVIKO",
    tagline = "Retirement savings for independent workers in Madagascar",
    save = "Save",
    cancel = "Cancel",
    ok = "OK",
    back = "Back",
    currency = "Ar",
    introTitle = "Prepare your retirement",
    introParagraph1 = "HOAVIKO helps Malagasy self-employed workers build retirement savings with a bank or a microfinance institution.",
    introParagraph2 = "Simulate your savings growth, make regular deposits and track your capital until age 60.",
    introParagraph3 = "Your savings remain accessible from age 50. Register for free to get started.",
    loginButton = "Log in",
    registerButton = "Create an account",
    loginScreenTitle = "Sign in",
    identifierLabel = "Username or email",
    passwordLabel = "Password",
    loginSubmit = "Log in",
    forgotPassword = "Forgot password?",
    registerLink = "Create an account",
    loginSuccess = "Signed in successfully. Welcome!",
    registerScreenTitle = "Create account",
    lastNameLabel = "Last name",
    firstNameLabel = "First names",
    birthDateLabel = "Birth date",
    cinLabel = "CIN number",
    professionLabel = "Profession",
    emailLabel = "Email",
    createPasswordLabel = "Create a password",
    confirmPasswordLabel = "Confirm password",
    registerSubmit = "Register",
    registerSuccess = "Account created successfully!",
    forgotTitle = "Reset password",
    forgotSubtitle = "Enter your email and CIN number to verify your identity.",
    forgotEmailLabel = "Account email",
    forgotCinLabel = "CIN number",
    newPasswordLabel = "New password",
    confirmNewPasswordLabel = "Confirm new password",
    forgotSubmit = "Reset",
    resetSuccess = "Password reset. Sign in!",
    greeting = "Hi, {name}!",
    memberRank = "Member # {rank}",
    objectiveLabel = "My objective",
    objectiveProgress = "{current} / {target}",
    capitalProjected = "Projected capital at {age}",
    totalPaid = "Total paid",
    paymentsCount = "Number of deposits",
    monthlyContribution = "Monthly contribution",
    simulateButton = "Simulate a new plan",
    editSettingsButton = "Edit my account",
    defineObjectiveHint = "Set your objective in My profile to track your progress.",
    simTitle = "Savings simulation",
    simSubtitle = "Estimate your retirement capital based on your contributions.",
    ageNowLabel = "Current age",
    ageRetirementLabel = "Retirement age",
    monthlyLabel = "Monthly contribution",
    initialLabel = "Initial savings",
    rateLabel = "Annual rate",
    institutionLabel = "Savings institution",
    resultTitle = "Estimated capital over {years} years",
    applyButton = "Apply to my profile",
    invalidInputHint = "Enter valid values (retirement age > current age, contribution > 0).",
    trackTitle = "Account · Tracking",
    newDepositTitle = "Deposit now",
    amountLabel = "Amount",
    paymentModeLabel = "Payment method",
    dateLabel = "Date",
    noteLabel = "Note (optional)",
    depositButton = "Confirm deposit",
    passwordConfirmLabel = "Confirm with your password",
    depositDialogTitle = "Deposit confirmation",
    depositConfirmHint = "Once confirmed, the transaction is final and cannot be undone.",
    depositSuccessTitle = "Deposit completed, Congratulations!",
    depositSuccessLine = "You have {amount}. Updated total: {total}.",
    historyTitle = "Deposit history",
    filterAll = "All",
    filterWeek = "Week",
    filterMonth = "Month",
    filterYear = "Year",
    emptyHistory = "No deposits yet.",
    periodNoResult = "No deposits in this period.",
    chooseChannel = "Select",
    phoneNumberLabel = "Phone number",
    bankAccountLabel = "Bank account number",
    settingsTitle = "My profile",
    usernameLabel = "Username",
    fullNameLabel = "Last name and first names",
    professionValueLabel = "Profession",
    languageLabel = "Language",
    langFr = "French",
    langEn = "English",
    langMg = "Malagasy",
    savingsBankLabel = "Savings institution",
    objectiveField = "Savings objective (Ar)",
    monthlyField = "Target monthly contribution (Ar)",
    saveSettingsButton = "Save",
    currentPasswordLabel = "Current password",
    changePasswordButton = "Change password",
    changePasswordConfirmTitle = "Confirm password change",
    changePasswordConfirmHint = "Do you really want to change your password?",
    retirementInfo = "Retirement age: {age}. Savings accessible from {access}.",
    logoutButton = "Log out",
    logoutMessage = "You have been logged out.",
    withdrawalButton = "Make a withdrawal",
    withdrawalDialogTitle = "Savings withdrawal request",
    withdrawalConfirmHint = "The request will be sent to the HOAVIKO manager for validation. It cannot be cancelled.",
    noticeFieldLabel = "Withdrawal type",
    noticeWithLabel = "With notice (7 d)",
    noticeWithoutLabel = "Immediate",
    penaltyPreview = "HOAVIKO penalty: {percent}% ({amount})",
    justificationLabel = "Withdrawal justification",
    justificationHint = "E.g.: health costs, school fees, urgent need…",
    justificationLine = "Justification: {reason}",
    withdrawalSubmitButton = "Send request",
    myWithdrawalRequests = "My withdrawal requests",
    emptyWithdrawals = "No withdrawal requests yet.",
    statusPending = "Pending",
    statusApproved = "Approved",
    statusRejected = "Rejected",
    requestedOn = "Requested on {date}",
    withdrawnOn = "Withdrawal on {date}",
    penaltyLine = "Penalty: {percent}% ({amount})",
    netLine = "Net received: {amount}",
    withdrawalSuccessTitle = "Request sent",
    withdrawalSuccessLine = "Withdrawal {amount} · Penalty {penalty} ({percent}%) · Net received {net}.",
    adminTitle = "HOAVIKO Administration",
    adminRequestsTitle = "Received withdrawal requests",
    emptyAdminRequests = "No withdrawal requests yet.",
    approveButton = "Approve",
    rejectButton = "Reject",
    approveConfirmTitle = "Approve request",
    approveConfirmHint = "Validate the withdrawal of {amount}?",
    rejectConfirmTitle = "Reject request",
    rejectConfirmHint = "Reject the withdrawal of {amount}?",
    msgWithdrawalApproved = "Request approved.",
    msgWithdrawalRejected = "Request rejected.",
    adminTabUsers = "Users",
    emptyAdminUsers = "No registered users.",
    deleteUserButton = "Delete",
    deleteUserConfirmTitle = "Delete account",
    deleteUserConfirmHint = "Permanently delete the account of {name} ({username})?",
    deleteReasonLabel = "Deletion reason",
    deleteReasonLine = "Reason: {reason}",
    msgUserDeleted = "Account deleted.",
    acceptPolicyLabel = "I accept the terms of use",
    readPolicyLink = "Read the terms of use",
    policyTitle = "Terms of use",
    policyBody = "Terms of use — HOAVIKO\n" +
        "Last updated: 15/09/2026\n\n" +
        "1. Purpose of the service\n" +
        "HOAVIKO is a retirement savings app for self-employed workers in Madagascar. It lets you simulate the growth of your savings, make regular deposits, track your capital and make withdrawals, up to the retirement age of 60. The app is purely informational and local: it is neither a financial institution nor an investment adviser, and it guarantees no return.\n\n" +
        "2. Acceptance of terms\n" +
        "Any registration on HOAVIKO constitutes acceptance of these terms. Users are invited to read them before creating an account.\n\n" +
        "3. User account\n" +
        "Users undertake to provide accurate, up-to-date information: last name, first names, CIN number, email and date of birth.\n" +
        "Only one account is allowed per person.\n" +
        "Users are solely responsible for the confidentiality of their username and password, and for any activity carried out on their account.\n" +
        "4. Confidentiality and data storage\n" +
        "Data is stored locally on the device: the app is fully offline and neither collects nor shares any personal data with a server.\n" +
        "Passwords are hashed (SHA-256 + salt) and never stored in plain text.\n" +
        "Users can view and edit their information from the “My profile” screen.\n" +
        "5. Simulation and projections\n" +
        "Simulation results (projected capital, annual rates) are indicative only: they are not a promise of return or contractual financial advice.\n\n" +
        "6. Deposits\n" +
        "A deposit becomes final after confirmation with the user's password.\n" +
        "The deposit history cannot be deleted.\n" +
        "The unit amount is limited according to the chosen channel (Mobile Money or bank).\n" +
        "Savings are normally accessible from age 50.\n" +
        "7. Withdrawals\n" +
        "Withdrawing part of your savings is possible subject to the following rules:\n\n" +
        "Age at withdrawal\tNotice of at least 7 days\tHOAVIKO penalty (on the withdrawn amount)\n" +
        "Under 50\tNo\t10 %\n" +
        "Under 50\tYes\t5 %\n" +
        "50 and over\t—\t0 %\n" +
        "A notice consists of a withdrawal request recorded in the app at least 7 calendar days before the desired withdrawal date. The request is dated and traced.\n" +
        "Without notice, an early withdrawal (before 50) is subject to a 10 % penalty on the withdrawn amount.\n" +
        "With proper notice, the penalty is reduced to 5 % of the withdrawn amount.\n" +
        "From age 50 (including up to and beyond retirement at 60), withdrawals are penalty-free.\n" +
        "Net amount received = withdrawn amount − penalty.\n" +
        "A withdrawal is final and irreversible once confirmed with your password; its history cannot be deleted.\n" +
        "8. Usage restrictions\n" +
        "Strictly prohibited: identity theft, false declarations (notably of age or notice), and any fraudulent use of the service.\n" +
        "9. Liability\n" +
        "HOAVIKO cannot be held responsible for data loss resulting from uninstalling the app, resetting the device or lack of backup. The rates, penalties and projections shown may change.\n" +
        "10. Account suspension\n" +
        "An account may be suspended or closed by the administrator in case of non-compliance with these terms.\n" +
        "11. Amendments\n" +
        "These terms may be updated. The version in force is the one displayed in the app.",
    msgAcceptPolicy = "Please accept the terms of use to create an account.",
    msgFillAllFields = "Please fill in all fields.",
    msgInvalidEmail = "Invalid email address.",
    msgEmailTaken = "An account already exists with this email.",
    msgPasswordShort = "Password must be at least 6 characters.",
    msgPasswordMismatch = "Passwords do not match.",
    msgInvalidCredentials = "Incorrect username or password.",
    msgAccountNotFound = "No account found with this email.",
    msgCinMismatch = "The CIN number does not match this account.",
    msgSettingsSaved = "Settings saved.",
    msgInvalidAmount = "Enter a valid amount.",
    msgWrongPassword = "Incorrect password.",
    msgPasswordChanged = "Password changed successfully.",
    msgInsufficientBalance = "Insufficient balance, please check your amount.",
    msgJustificationRequired = "Please specify the reason for your withdrawal.",
    msgInternetRequired = "An Internet connection is required to create an account.",
    msgOnlineRegistrationFailed = "Online account creation failed. Please try again."
)

val MalagasyStrings = AppStrings(
    appName = "HOAVIKO",
    tagline = "Fitehirizana fisotroan-dronono ho an'ny miasa samirery eto Madagasikara",
    save = "Tehirizo",
    cancel = "Ajanony",
    ok = "OK",
    back = "Miverina",
    currency = "Ariary",
    introTitle = "Omano ny fisotroan-dronono",
    introParagraph1 = "Manampy ny miasa samirery eto Madagasikara i HOAVIKO hanorina fitehirizana fisotroan-dronono ao amin'ny banky na institosiona microfinance.",
    introParagraph2 = "Simulà ny fitombon'ny fitehirizanao, manaova likely ara-potoana ary araho ny fanananao hatramin'ny 60 taona.",
    introParagraph3 = "Azo alaina hatramin'ny 50 taona ny fitehirizana. Midira maimaim-poana hanomboka.",
    loginButton = "Hiditra",
    registerButton = "Hamorona kaonty",
    loginScreenTitle = "Hiditra",
    identifierLabel = "Sokajin-tena na mailaka",
    passwordLabel = "Teny miafina",
    loginSubmit = "Hiditra",
    forgotPassword = "Adino ny teny miafina?",
    registerLink = "Hamorona kaonty",
    loginSuccess = "Tafiditra soa aman-tsara. Tongasoa!",
    registerScreenTitle = "Famoronana kaonty",
    lastNameLabel = "Anarana",
    firstNameLabel = "Fanampin'anarana",
    birthDateLabel = "Daty nahaterahana",
    cinLabel = "Laharana CIN",
    professionLabel = "Asa",
    emailLabel = "Mailaka",
    createPasswordLabel = "Mamorona teny miafina",
    confirmPasswordLabel = "Hamafiso ny teny miafina",
    registerSubmit = "Hisoratra",
    registerSuccess = "Vita soa aman-tsara ny kaonty!",
    forgotTitle = "Averina ny teny miafina",
    forgotSubtitle = "Ampidiro ny mailaka sy ny laharana CIN hanamarinana ny maha-ianao.",
    forgotEmailLabel = "Mailakan'ny kaonty",
    forgotCinLabel = "Laharana CIN",
    newPasswordLabel = "Teny miafina vaovao",
    confirmNewPasswordLabel = "Hamafiso ny teny miafina vaovao",
    forgotSubmit = "Averina",
    resetSuccess = "Noverina ny teny miafina. Hidira!",
    greeting = "Salama, {name}!",
    memberRank = "Mpikambana laharana {rank}",
    objectiveLabel = "Tanjoko",
    objectiveProgress = "{current} / {target}",
    capitalProjected = "Fananana efa vinavinaina @ {age} taona",
    totalPaid = "Total vola nalefa",
    paymentsCount = "Isan'ny likely",
    monthlyContribution = "Fandraisana isam-bolana",
    simulateButton = "Simulà drafitra vaovao",
    editSettingsButton = "Ahitsio ny kaontiko",
    defineObjectiveHint = "Mametraha tanjona ao amin'ny Ny profil-ko raha hanara-maso ny fandrosoanao.",
    simTitle = "Simulation'ny fitehirizana",
    simSubtitle = "Tombanà ny fanananao amin'ny fisotroan-dronono arakaraka ny fandraisanao.",
    ageNowLabel = "Taona ankehitriny",
    ageRetirementLabel = "Taona hifototra",
    monthlyLabel = "Fandraisana isam-bolana",
    initialLabel = "Fitehirizana fanombohana",
    rateLabel = "Tahan'ny zana-bola",
    institutionLabel = "Andrim-panjakana fitehirizana",
    resultTitle = "Fananana tombanà amin'ny {years} taona",
    applyButton = "Ampiharina amin'ny profil-ko",
    invalidInputHint = "Ampidiro soatoavina marina (taona hifototra > taona ankehitriny, fandraisana > 0).",
    trackTitle = "Esepace kaonty · Fanarahana",
    newDepositTitle = "Alefaso izao",
    amountLabel = "Volavola",
    paymentModeLabel = "Fomba fandoavam-bola",
    dateLabel = "Daty",
    noteLabel = "Fanamarika (tsilisy)",
    depositButton = "Hamafiso ny likely",
    passwordConfirmLabel = "Hamafiso amin'ny teny miafina",
    depositDialogTitle = "Fanamarinana ny likely",
    depositConfirmHint = "Raha voadona ny likely dia tsy azo averina intsony.",
    depositSuccessTitle = "Vita ny likely, Arahabaina!",
    depositSuccessLine = "Manana {amount} ianao. Total vaovao: {total}.",
    historyTitle = "Tantara ny likely",
    filterAll = "Total",
    filterWeek = "Herinandro",
    filterMonth = "Volana",
    filterYear = "Taona",
    emptyHistory = "Mbola tsy misy likely.",
    periodNoResult = "Tsy misy likely tamin'ity vanim-potoana ity.",
    chooseChannel = "Fidio",
    phoneNumberLabel = "Laharan'ny finday",
    bankAccountLabel = "Laharan'ny kaonty banky",
    settingsTitle = "Ny profil-ko",
    usernameLabel = "Sokajin-tena",
    fullNameLabel = "Anarana sy fanampin'anarana",
    professionValueLabel = "Asa",
    languageLabel = "Fiteny",
    langFr = "Frantsay",
    langEn = "Anglisy",
    langMg = "Malagasy",
    savingsBankLabel = "Andrim-panjakana fitehirizana",
    objectiveField = "Tanjon'ny fitehirizana (Ariary)",
    monthlyField = "Fandraisana isam-bolana kendrena (Ariary)",
    saveSettingsButton = "Tehirizo",
    currentPasswordLabel = "Teny miafina ankehitriny",
    changePasswordButton = "Hanova ny teny miafina",
    changePasswordConfirmTitle = "Hanamafy ny fanovana ny teny miafina",
    changePasswordConfirmHint = "Tianao ve ny manova ny teny miafina?",
    retirementInfo = "Taona hifototra : {age}. Azo alaina hatramin'ny {access} taona.",
    logoutButton = "Hivoaka",
    logoutMessage = "Nivoaka ianao.",
    withdrawalButton = "Misintona vola",
    withdrawalDialogTitle = "Fangatahana fisintomana vola",
    withdrawalConfirmHint = "Halefa amin'ny mpitantana HOAVIKO ny fangatahana ho fanamarinana. Tsy azo averina intsony.",
    noticeFieldLabel = "Karazana fisintomana",
    noticeWithLabel = "Miaraka amin'ny fanambaràna (7 andro)",
    noticeWithoutLabel = "Avy hatrany",
    penaltyPreview = "Sazy HOAVIKO : {percent} % ({amount})",
    justificationLabel = "Antony amin'ny fisintomana",
    justificationHint = "Ohatra: fandaniana ara-pahasalamana, fianarana, ilaina haingana…",
    justificationLine = "Antony: {reason}",
    withdrawalSubmitButton = "Alefaso ny fangatahana",
    myWithdrawalRequests = "Ny fangatahana fisintomako",
    emptyWithdrawals = "Tsy mbola misy fangatahana.",
    statusPending = "Miandry",
    statusApproved = "Ekena",
    statusRejected = "Nolavina",
    requestedOn = "Nangatahana tamin'ny {date}",
    withdrawnOn = "Hisintona amin'ny {date}",
    penaltyLine = "Sazy : {percent} % ({amount})",
    netLine = "Vola azo : {amount}",
    withdrawalSuccessTitle = "Nalefa ny fangatahana",
    withdrawalSuccessLine = "Fisintomana {amount} · Sazy {penalty} ({percent} %) · Vola azo {net}.",
    adminTitle = "Fitantanana HOAVIKO",
    adminRequestsTitle = "Fangatahana fisintomana voaray",
    emptyAdminRequests = "Mbola tsy misy fangatahana fisintomana.",
    approveButton = "Ekena",
    rejectButton = "Lavina",
    approveConfirmTitle = "Hanamarina ny fangatahana",
    approveConfirmHint = "Hankatoavina ve ny fisintomana {amount}?",
    rejectConfirmTitle = "Handà ny fangatahana",
    rejectConfirmHint = "Holavina ve ny fisintomana {amount}?",
    msgWithdrawalApproved = "Ekena ny fangatahana.",
    msgWithdrawalRejected = "Nolavina ny fangatahana.",
    adminTabUsers = "Mpampiasa",
    emptyAdminUsers = "Mbola tsy misy mpampiasa.",
    deleteUserButton = "Fafana",
    deleteUserConfirmTitle = "Fafana ny kaonty",
    deleteUserConfirmHint = "Fafana tokoa ve ny kaontin'i {name} ({username})?",
    deleteReasonLabel = "Antony amin'ny famafana",
    deleteReasonLine = "Antony: {reason}",
    msgUserDeleted = "Voafafa ny kaonty.",
    acceptPolicyLabel = "Ekena ny politikan'ny fampiasana",
    readPolicyLink = "Vakio ny politikan'ny fampiasana",
    policyTitle = "Politikan'ny fampiasana",
    policyBody = "Politikan'ny fampiasana — HOAVIKO\n" +
        "Fanavaozana farany: 15/09/2026\n\n" +
        "1. Tanjona amin'ny serivisy\n" +
        "HOAVIKO dia rindranasa fitehirizana fisotroan-dronono ho an'ny mpiasa an-tena malagasy. Ahafahana manahaka ny fivoaran'ny tahiry, manao fampidirana tsy tapaka, manara-maso ny renivola ary misintona vola hatramin'ny taona fisotroan-dronono (60 taona). Rindranasa fampahafantarana fotsiny izy ary miasa eo an-toerana: tsy andrim-piantohana ara-bola izy, tsy mpanolotsaina momba ny fampiasam-bola, ary tsy manome antoka ihany koa amin'ny fiverenan'ny vola.\n\n" +
        "2. Fanekena ny fepetra\n" +
        "Ny fisoratana rehetra ao amin'ny HOAVIKO dia fanekena ity politika ity. Asaina vakina alohan'ny hamoronana kaonty.\n\n" +
        "3. Kaonty mpampiasa\n" +
        "Ny mpampiasa dia manolo-tena hanome vaovao marina sy farany: anarana, fanampin'anarana, laharan'ny CIN, mailaka ary daty nahaterahana.\n" +
        "Kaonty iray ihany no avela isaky ny olona.\n" +
        "Ny mpampiasa no tompon'andraikitra amin'ny tsiambaratelon'ny anarana idirana sy ny teny miafina, ary ny hetsika rehetra atao amin'ny kaontiny.\n" +
        "4. Tsiambaratelo sy fitahirizana ny angona\n" +
        "Voatahiry eo an-toerana amin'ny fitaovana ny angona: miasa tsy misy tambajotra tanteraka ny rindranasa, tsy manangona na mizara angona manokana amin'ny lohamilina.\n" +
        "Ny teny miafina dia hashina (SHA-256 + sel) ary tsy voatahiry mihitsy amin'ny endriny mazava.\n" +
        "Afaka mijery sy manova ny vaovao ao amin'ny efijery « Ny mombamomba ahy » ny mpampiasa.\n" +
        "5. Fanahaka sy vinavina\n" +
        "Ny vokatry ny fanahaka (renivola vinavinaina, taham-bola isan-taona) dia famantarana fotsiny: tsy fampanantenana fiverenana na torohevitra ara-bola manan-kery izy ireo.\n\n" +
        "6. Fampidirana vola\n" +
        "Ny fampidirana vola dia lasa raikitra rehefa voamarina amin'ny teny miafina.\n" +
        "Tsy azo vonoina ny tantaran'ny fampidirana vola.\n" +
        "Ny habetsahan'ny volavola iray dia ferana arakaraka ny fantsona voafidy (Mobile Money na banky).\n" +
        "Ny tahiry dia azo alaina amin'ny ankapobeny hatramin'ny 50 taona.\n" +
        "7. Fisintomana\n" +
        "Azo atao ny misintona ny ampahany amin'ny tahiry raha toa ka manaraka ireto fitsipika ireto:\n\n" +
        "Taona amin'ny fisintomana\tFampandrenesana 7 andro farafahakeliny\tSazy HOAVIKO (amin'ny vola esorina)\n" +
        "Latsaky ny 50 taona\tTsia\t10 %\n" +
        "Latsaky ny 50 taona\tEny\t5 %\n" +
        "50 taona na mihoatra\t—\t0 %\n" +
        "Ny fampandrenesana dia fangatahana fisintomana voarakitra ao amin'ny rindranasa 7 andro lavitra ny daty tiana hisintomana farafahakeliny. Voarakitra sy voamarika ny datin'ny fangatahana.\n" +
        "Raha tsy misy fampandrenesana, misy sazy 10 % amin'ny vola esorina ny fisintomana aloha lany (alohan'ny 50 taona).\n" +
        "Raha voahaja ny fampandrenesana, dia midina ho 5 % amin'ny vola esorina ny sazy.\n" +
        "Hatramin'ny 50 taona (anisan'izany ny fisotroan-dronono 60 taona sy ny manaraka), tsy misy sazy ny fisintomana.\n" +
        "Ny vola azo = vola esorina − sazy.\n" +
        "Rahikitra sy tsy azo averina intsony ny fisintomana rehefa voamarina amin'ny teny miafina; tsy azo vonoina ny tantarany.\n" +
        "8. Famerana ny fampiasana\n" +
        "Voarara tanteraka: ny fakana anarana an'olon-kafa, ny lainga (indrindra momba ny taona na ny fampandrenesana), ary ny fampiasana hosoka rehetra.\n" +
        "9. Andraikitra\n" +
        "Tsy tompon'andraikitra ny HOAVIKO amin'ny fahafoanan'ny angona vokatry ny famafana ny rindranasa, ny famerenana ny fitaovana na ny tsy fisian'ny tahiry. Mety hiova ny taham-bola, ny sazy ary ny vinavina aseho.\n" +
        "10. Famonoana kaonty\n" +
        "Azo vonoina na hikatona ny kaonty ataon'ny mpitantana raha tsy manaraka ity politika ity ny mpampiasa.\n" +
        "11. Fanovana\n" +
        "Azo havaozina ity politika ity. Ny dikan-teny mihatra dia ilay aseho ao amin'ny rindranasa.",
    msgAcceptPolicy = "Ekeo ny politikan'ny fampiasana aloha vao hamorona kaonty.",
    msgFillAllFields = "Fenoy daholo ny eto rehetra.",
    msgInvalidEmail = "Diso ny adiresy mailaka.",
    msgEmailTaken = "Efa misy kaonty amin'ity mailaka ity.",
    msgPasswordShort = "Farafahakeliny 6 ny isan'ny literan'ny teny miafina.",
    msgPasswordMismatch = "Tsy mitovy ny teny miafina roa.",
    msgInvalidCredentials = "Diso ny sokajin-tena na ny teny miafina.",
    msgAccountNotFound = "Tsy hita kaonty amin'ity mailaka ity.",
    msgCinMismatch = "Tsy mifanaraka amin'ity kaonty ity ny laharana CIN.",
    msgSettingsSaved = "Voatahiry ny fanovana.",
    msgInvalidAmount = "Ampidiro volavola marina.",
    msgWrongPassword = "Diso ny teny miafina.",
    msgPasswordChanged = "Novaina soa aman-tsara ny teny miafina.",
    msgInsufficientBalance = "Tsy ampy ny vola, mba hamarino ny volavola.",
    msgJustificationRequired = "Ampidiro ny antony amin'ny fisintomanao.",
    msgInternetRequired = "Ilaina ny fifandraisana Internet mba hamoronana kaonty.",
    msgOnlineRegistrationFailed = "Tsy nahomby ny famoronana kaonty an-tserasera. Andramo indray."

)