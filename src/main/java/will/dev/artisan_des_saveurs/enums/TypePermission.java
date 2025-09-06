package will.dev.artisan_des_saveurs.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TypePermission {
    ADMIN_CREATE,
    ADMIN_READ,
    ADMIN_UPDATE,
    ADMIN_DELETE,
    ADMIN_PATCH,

    MANAGER_CREATE,
    MANAGER_READ,
    MANAGER_UPDATE,
    MANAGER_DELETE,

    USER_CREATE,
    USER_READ,
    USER_DELETE,
    USER_UPDATE

}
