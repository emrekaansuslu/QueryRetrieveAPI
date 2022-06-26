package com.icterra.midas.ae.queryretrieve.util;

public enum ServiceStatusEnum {

    NoStatus(-1),
    Success(0),
    Pending(65280),
    PendingWarning(65281),
    Cancel(65024),
    NoSuchAttribute(261),
    InvalidAttributeValue(262),
    AttributeListError(263),
    ProcessingFailure(272),
    DuplicateSOPinstance(273),
    NoSuchObjectInstance(274),
    NoSuchEventType(275),
    NoSuchArgument(276),
    InvalidArgumentValue(277),
    AttributeValueOutOfRange(278),
    InvalidObjectInstance(279),
    NoSuchSOPclass(280),
    ClassInstanceConflict(281),
    MissingAttribute(288),
    MissingAttributeValue(289),
    SOPclassNotSupported(290),
    NoSuchActionType(291),
    NotAuthorized(292),
    DuplicateInvocation(528),
    UnrecognizedOperation(529),
    MistypedArgument(530),
    ResourceLimitation(531),
    OutOfResources(42752),
    UnableToCalculateNumberOfMatches(42753),
    UnableToPerformSubOperations(42754),
    MoveDestinationUnknown(43009),
    IdentifierDoesNotMatchSOPClass(43264),
    DataSetDoesNotMatchSOPClassError(43264),
    OneOrMoreFailures(45056),
    CoercionOfDataElements(45056),
    ElementsDiscarded(45062),
    DataSetDoesNotMatchSOPClassWarning(45063),
    UnableToProcess(49152),
    CannotUnderstand(49152),
    UPSCreatedWithModifications(45824),
    UPSDeletionLockNotGranted(45825),
    UPSAlreadyInRequestedStateOfCanceled(45828),
    UPSCoercedInvalidValuesToValidValues(45829),
    UPSAlreadyInRequestedStateOfCompleted(45830),
    UPSMayNoLongerBeUpdated(49920),
    UPSTransactionUIDNotCorrect(49921),
    UPSAlreadyInProgress(49922),
    UPSStateMayNotChangedToScheduled(49923),
    UPSNotMetFinalStateRequirements(49924),
    UPSDoesNotExist(49927),
    UPSUnknownReceivingAET(49928),
    UPSNotScheduled(49929),
    UPSNotYetInProgress(49936),
    UPSAlreadyCompleted(49937),
    UPSPerformerCannotBeContacted(49938),
    UPSPerformerChoosesNotToCancel(49939),
    UPSActionNotAppropriate(49940),
    UPSDoesNotSupportEventReports(49941),
    ErrorInResults(99998),
    NoFindQueryResults(99999);

    private int value;

    ServiceStatusEnum(int value) {
        this.value = value;
    }

    public static ServiceStatusEnum convertToServiceStatus(int value)
    {
        for (ServiceStatusEnum serviceStatusEnum : ServiceStatusEnum.values()) {
            if (serviceStatusEnum.getValue() == value)
            {
                return serviceStatusEnum;
            }
        }
        return ServiceStatusEnum.NoStatus;
    }

    public int getValue() {
        return value;
    }
}
