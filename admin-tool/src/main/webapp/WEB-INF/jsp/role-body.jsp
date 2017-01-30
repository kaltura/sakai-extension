<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="panel panel-default">
    <div class="panel-heading accordion-toggle bold" data-toggle="collapse" data-parent="#accordion" data-target="#collapse-role">
        <h4 class="panel-title">Custom role mapping</h4>
    </div>
    <div id="collapse-role" class="panel-body panel-collapse collapse in">
        <form id="custom-role-form">
            <table id="custom-role-table" class="table table-hover">
                <tr>
                    <th class="column-role column-role-sakai">Sakai Site Role</th>
                    <th class="column-role column-role-lti">LTI Role</th>
                    <th class="column-role column-role-buttons"></th>
                </tr>
                <tr class="custom-role-table-row">
                    <td class="custom-role-sakai">
                        <span class="custom-role-sakai-text"></span>
                        <select class="custom-role-sakai-select form-control collapse"></select>
                    </td>
                    <td class="custom-role-lti">
                        <span class="custom-role-lti-text"></span>
                        <select class="custom-role-lti-select form-control collapse"></select>
                    </td>
                    <td class="custom-role-button">
                        <input type="hidden" class="custom-role-id" value="new" />
                        <div>
                            <button class="custom-role-button-edit btn btn-primary">
                                <span class="glyphicon glyphicon-pencil"></span> Edit
                            </button>
                            <button class="custom-role-button-save btn btn-primary collapse">Save</button>
                            <button class="custom-role-button-cancel btn btn-danger collapse">Cancel</button>
                            <button class="custom-role-button-delete btn btn-danger collapse">
                                <span class="glyphicon glyphicon-remove"></span> Delete
                            </button>
                        </div>
                        <div class="custom-role-status alert">
                            <a href="#" class="close" data-dismiss="alert"><span class="glyphicon"></span></a>
                        </div>
                    </td>
                </tr>
                <tr class="custom-role-add-table-row">
                    <td colspan="3">
                        <button class="custom-role-button-add btn btn-primary">Add</button>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>