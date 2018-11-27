package us.oh.state.epa.stars2.portal.home;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.webcommon.AppBase;

public class Task extends AppBase {
    private String taskName;
    private TaskType taskType;
    private String taskDescription;
    private Integer taskInternalId;
    private Integer id;
    private boolean dependent;
    private Timestamp createDate;
    private String userName;
    private String version;

    //static enum TaskType {
    //    TVPA, TIVPA, PTPA, PBR, FC, ER
    //}
    
    static enum TaskType {
        COPY_PTPA, COPY_TVPA, CR_CEMS, CR_GENERIC, CR_OTHR, CR_PER, CR_SMBR, 
        CR_TEST, CR_TVCC, CR_ONE, DOR, ER, FC, FCC, FCH, PBR, PTPA, R_ER, REL, 
        RPC, ST, TIVPA, TVPA, MRPT
    }

    public final Integer getTaskInternalId() {
        return taskInternalId;
    }

    public final void setTaskInternalId(final Integer taskInternalId) {
        this.taskInternalId = taskInternalId;
    }

    public final String getTaskDescription() {
        return taskDescription;
    }

    public final void setTaskDescription(final String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public final String getTaskName() {
        return taskName;
    }

    public final void setTaskName(final String taskName) {
        this.taskName = taskName;
    }

    public final TaskType getTaskType() {
        return taskType;
    }

    public final void setTaskType(final TaskType taskType) {
        this.taskType = taskType;
    }

    public final Timestamp getCreateDate() {
        return createDate;
    }

    public final void setCreateDate(final Timestamp createDate) {
        this.createDate = createDate;
    }

    public final boolean getDependent() {
        return dependent;
    }

    public final void setDependent(final boolean dependent) {
        this.dependent = dependent;
    }

    public final Integer getId() {
        return id;
    }

    public final void setId(final Integer id) {
        this.id = id;
    }

    public final String getUserName() {
        return userName;
    }

    public final void setUserName(final String userName) {
        this.userName = userName;
    }

    public final String getVersion() {
        return version;
    }

    public final void setVersion(final String version) {
        this.version = version;
    }
}
